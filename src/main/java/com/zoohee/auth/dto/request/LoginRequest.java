package com.zoohee.auth.dto.request;

public record LoginRequest(
        String username,
        String password
) {
}
