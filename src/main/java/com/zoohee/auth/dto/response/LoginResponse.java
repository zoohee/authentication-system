package com.zoohee.auth.dto.response;

import com.zoohee.auth.dto.TokenDto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
    public static LoginResponse from(TokenDto tokenDto) {
        return new LoginResponse(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
    }
}
