package com.zoohee.auth.common.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoohee.auth.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    public static final String AUTHORIZATION_KEY = "auth";
    private final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 토큰 만료시간 30분
    private final long REFRESH_TOKEN_EXPIRE_TIME = 10000 * 60 * 1000L; // 토큰 만료시간 10000분

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public TokenDto generateTokenDto(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = generateAccessToken(authentication.getName(), authorities);
        String refreshToken = generateRefreshToken(authentication.getName(), authorities);
        long now = (new Date()).getTime();
        return TokenDto.of(accessToken, refreshToken, new Date(now + ACCESS_TOKEN_EXPIRE_TIME));
    }

    private String generateAccessToken(String username, String authorities) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        return Jwts.builder()
                .setSubject(username)
                .claim(AUTHORIZATION_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    private String generateRefreshToken(String username, String authorities) {
        long now = (new Date()).getTime();
        return Jwts.builder()
                .setSubject(username)
                .claim(AUTHORIZATION_KEY, authorities)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .claim("isRefreshToken", true)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public TokenStatus validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
            return TokenStatus.EXPIRED;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return TokenStatus.INVALID;
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (claims.get(AUTHORIZATION_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        List<SimpleGrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORIZATION_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            Date expiration = claims.getExpiration();
            return !expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String generateAccessTokenByRefreshToken(String refreshToken) throws JsonProcessingException {
        // 1. redis에서 refresh token 확인하고 refresh toekn 유효하면 access token 발급
        Claims refreshClaims = parseClaims(refreshToken);
        String username = refreshClaims.getSubject();

        // 2. Redis에서 사용자 정보 조회
        String userDataJson = (String) redisTemplate.opsForValue().get(username);
        if (userDataJson == null) {
            throw new IllegalArgumentException("No data found in Redis for user: " + username);
        }

        // 3. refresh token 검증
        TokenDto userToken = objectMapper.readValue(userDataJson, TokenDto.class);

        if (!refreshToken.equals(userToken.getRefreshToken()) || isTokenExpired(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired Refresh Token");
        }

        // 4. 새로운 Access Token 생성
        String authorities = refreshClaims.get(AUTHORIZATION_KEY, String.class);
        String newAccessToken = generateAccessToken(username, authorities);
        Claims accessClaims = parseClaims(newAccessToken);
        TokenDto newUserToken = TokenDto.of(newAccessToken, refreshToken, accessClaims.getExpiration());
        redisTemplate.opsForValue().set(username, newUserToken);

        return newAccessToken;
    }
}