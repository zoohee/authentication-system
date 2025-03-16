package com.zoohee.auth.common.exception.exceptions;

import com.zoohee.auth.common.exception.ErrorCode;

public class AdminAccessDeniedException extends DefaultException {
    public AdminAccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED.getMessage());
    }
}
