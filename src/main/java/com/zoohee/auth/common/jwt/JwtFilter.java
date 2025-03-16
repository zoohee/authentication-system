package com.zoohee.auth.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        String requestURI = request.getRequestURI();

        // 특정 경로 필터링 제외
        if (requestURI.startsWith("/login") || requestURI.startsWith("/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Request Header 에서 토큰을 꺼냄
        String jwt = resolveToken(request);

        // 2. validateToken 으로 토큰 유효성 검사
        if (jwt != null && jwtUtil.validateToken(jwt) == TokenStatus.VALID) {
            Authentication authentication = jwtUtil.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (jwt != null && jwtUtil.validateToken(jwt) == TokenStatus.EXPIRED) {
            // Access Token 만료 시 Refresh Token 확인
            String refreshToken = getRefreshTokenFromCookies(request);

            // Refresh Token이 유효하면 새로운 Access Token 발급
            if (refreshToken != null && jwtUtil.validateToken(refreshToken) == TokenStatus.VALID) {
                String newAccessToken = jwtUtil.generateAccessTokenByRefreshToken(refreshToken);
                response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken);
            } else if (jwtUtil.validateToken(refreshToken) == TokenStatus.EXPIRED) {
                // todo: Refresh Token 만료 상태
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}