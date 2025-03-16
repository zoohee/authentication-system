package com.zoohee.auth.dto.response;

import com.zoohee.auth.entity.User;

public record GrantAdminResponse(
        String username,
        String nickname,
        String role
) {
    public static GrantAdminResponse from(User updateUser) {
        return new GrantAdminResponse(
                updateUser.getUsername(),
                updateUser.getNickname(),
                updateUser.getRole().name());
    }
}
