package com.zoohee.auth.controller;

import com.zoohee.auth.dto.request.SignupRequest;
import com.zoohee.auth.dto.response.LoginResponse;
import com.zoohee.auth.dto.response.SignupResponse;
import com.zoohee.auth.dto.request.LoginRequest;
import com.zoohee.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "인증 API", description = "회원가입 및 로그인 관련 API")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "서버 상태 확인", description = "서버가 정상적으로 작동하는지 확인하는 API")
    @GetMapping("/health-check")
    public ResponseEntity<String> healthcheck() {
        log.info("[AuthController] [health-check] ::: no request");
        return ResponseEntity.ok("Service is running");
    }

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록하는 API")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("[AuthController] [signup] request ::: {}", request);
        SignupResponse response = userService.signup(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인", description = "기존 사용자가 로그인하는 API")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("[AuthController] [login] request ::: {}", request);
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
