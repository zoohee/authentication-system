package com.zoohee.auth.dto.response;

import com.zoohee.auth.entity.User;
import com.zoohee.auth.entity.UserRole;

public record SignupResponse(
        String username,
        String nickname,
        String role
) {
    public static SignupResponse from(User user) {
        return new SignupResponse(
                user.getUsername(),
                user.getNickname(),
                user.getRole().name()
        );
    }
}
