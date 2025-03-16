package com.zoohee.auth.controller;

import com.zoohee.auth.common.exception.exceptions.AdminAccessDeniedException;
import com.zoohee.auth.dto.response.GrantAdminResponse;
import com.zoohee.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
@Tag(name = "관리자 API", description = "관리자 권한 관련 API")
public class AdminController {
    private final UserService userService;

    @Operation(
            summary = "관리자 권한 부여",
            description = "ADMIN 권한을 가진 사용자가 특정 사용자의 권한을 관리자(ADMIN)로 변경하는 API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
//    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<GrantAdminResponse> grantAdminRole(@AuthenticationPrincipal UserDetails userDetails,
                                                             @PathVariable @Parameter(description = "권한을 변경할 사용자 ID") Long userId) {
        log.info("[AuthController] [grantAdminRole] requested user's username ::: {}", userDetails.getUsername());
        log.info("[AuthController] [grantAdminRole] requested user's Role ::: {}", userDetails.getAuthorities());

        if (userDetails.getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AdminAccessDeniedException();
        }

        GrantAdminResponse response = userService.grantAdminRole(userId);
        return ResponseEntity.ok(response);
    }
}
