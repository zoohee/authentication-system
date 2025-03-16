package com.zoohee.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoohee.auth.common.config.SecurityConfig;
import com.zoohee.auth.common.exception.exceptions.AdminAccessDeniedException;
import com.zoohee.auth.common.exception.exceptions.UsernameDuplicatedException;
import com.zoohee.auth.config.TestConfig;
import com.zoohee.auth.dto.request.SignupRequest;
import com.zoohee.auth.dto.response.GrantAdminResponse;
import com.zoohee.auth.service.UserService;
import java.util.Collection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("[Controller] Admin Controller Unit Test")
@Import({TestConfig.class, SecurityConfig.class})
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "adminuser", roles = {"ADMIN"})
    @DisplayName("[관리자 권한 부여 성공] 정상적인 요청")
    void grantAdminRole_Success() throws Exception {
        // Given
        Long userId = 1L;
        GrantAdminResponse response = new GrantAdminResponse("testuser","Developer",  "ROLE_ADMIN");

        Mockito.when(userService.grantAdminRole(anyLong())).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/users/{userId}/roles", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

//    @Test
//    @WithMockUser(username = "normalUser", roles = {"USER"})
//    @DisplayName("[관리자 권한 부여 실패] 잘못된 권한 접근 제어")
//    void grantAdminRole_AccessDenied() throws Exception {
//        // Given
//        Long userId = 1L;
//
//        // When & Then
//        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/users/{userId}/roles", userId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
}
