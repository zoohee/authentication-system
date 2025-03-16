package com.zoohee.auth.dto.response;

import com.zoohee.auth.common.exception.ErrorCode;

public record ErrorResponse(
        ErrorCode errorCode,
        String message
) {
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }
}
