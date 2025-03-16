package com.zoohee.auth.service;

import com.zoohee.auth.dto.TokenDto;
import com.zoohee.auth.dto.request.SignupRequest;
import com.zoohee.auth.dto.response.GrantAdminResponse;
import com.zoohee.auth.dto.response.LoginResponse;
import com.zoohee.auth.dto.response.SignupResponse;
import com.zoohee.auth.entity.User;
import com.zoohee.auth.entity.UserRole;
import com.zoohee.auth.common.exception.exceptions.PasswordNotMatchedException;
import com.zoohee.auth.common.exception.exceptions.UserNotFoundException;
import com.zoohee.auth.common.exception.exceptions.UsernameDuplicatedException;
import com.zoohee.auth.common.jwt.JwtUtil;
import com.zoohee.auth.repository.UserRepository;
import com.zoohee.auth.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        log.debug("[UserService] [signup] username ::: {}, nickname ::: {}", request.username(), request.nickname());

        userRepository.findByUsername(request.username()).ifPresent(member -> {
            throw new UsernameDuplicatedException(member.getUsername());
        });

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = userRepository.save(User.of(request, encodedPassword));

        return SignupResponse.from(user);
    }

    public LoginResponse login(LoginRequest request) {
        log.debug("[UserService] [login] username ::: {}", request.username());

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(UserNotFoundException::new);

        isNotMatchedPassword(request.password(), user.getPassword());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDto tokenDto = jwtUtil.generateTokenDto(authentication);
        redisTemplate.opsForValue().set(request.username(), tokenDto);

        return LoginResponse.from(tokenDto);
    }

    @Transactional
    public GrantAdminResponse grantAdminRole(Long userId) {
        log.debug("[UserService] [grantAdminRole] userId ::: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updateRole(UserRole.ADMIN);
        User updateUser = userRepository.save(user);

        return GrantAdminResponse.from(updateUser);
    }

    private void isNotMatchedPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new PasswordNotMatchedException();
        }
    }
}
