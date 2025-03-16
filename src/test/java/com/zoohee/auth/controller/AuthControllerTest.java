package com.zoohee.auth.controller;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoohee.auth.common.exception.exceptions.PasswordNotMatchedException;
import com.zoohee.auth.common.exception.exceptions.UsernameDuplicatedException;
import com.zoohee.auth.config.TestConfig;
import com.zoohee.auth.dto.request.LoginRequest;
import com.zoohee.auth.dto.request.SignupRequest;
import com.zoohee.auth.dto.response.LoginResponse;
import com.zoohee.auth.dto.response.SignupResponse;
import com.zoohee.auth.entity.User;
import com.zoohee.auth.entity.UserRole;
import com.zoohee.auth.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@DisplayName("[Controller] Auth Controller Unit Test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("[회원가입 성공 테스트] 올바른 정보를 입력하면 성공")
    void signupTest_Success() throws Exception {
        // Given
        String requestUrl = "/signup";
        SignupRequest signupRequest = new SignupRequest("testuser", "Password123!", "Developer", UserRole.ADMIN);
        String requestBody = objectMapper.writeValueAsString(signupRequest);

        Mockito.when(userService.signup(any(SignupRequest.class)))
                .then(invocation -> {
                    String encodedPassword = passwordEncoder.encode(signupRequest.password());
                    User user = User.of(signupRequest, encodedPassword);
                    return SignupResponse.from(user);
                });

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(requestUrl)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("[회원가입 실패 테스트] 중복된 아이디 입력 시 실패")
    void signupTest_DuplicatedUsername() throws Exception {
        // Given
        String requestUrl = "/signup";
        SignupRequest signupRequest = new SignupRequest("existuser", "Password123!", "Developer", UserRole.ADMIN);
        String requestBody = objectMapper.writeValueAsString(signupRequest);

        Mockito.when(userService.signup(any(SignupRequest.class)))
                .thenThrow(new UsernameDuplicatedException(signupRequest.username()));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(requestUrl)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @DisplayName("[로그인 성공 테스트] 올바른 정보를 입력하면 성공")
    void loginTest_Success() throws Exception {
        // Given
        String requestUrl = "/login";
        LoginRequest loginRequest = new LoginRequest("testuser", "Password123!");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        LoginResponse loginResponse = new LoginResponse("mocked-access-token", "mocked-refresh-token");

        Mockito.when(userService.login(any(LoginRequest.class)))
                .thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(requestUrl)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("[로그인 실패 테스트] 잘못된 비밀번호 입력 시 실패")
    void loginTest_Fail_InvalidPassword() throws Exception {
        // Given
        String requestUrl = "/login";
        LoginRequest loginRequest = new LoginRequest("testuser", "WrongPassword!");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        Mockito.when(userService.login(any(LoginRequest.class)))
                .thenThrow(new PasswordNotMatchedException());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(requestUrl)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}