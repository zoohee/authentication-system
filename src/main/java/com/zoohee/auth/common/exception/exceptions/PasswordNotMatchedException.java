package com.zoohee.auth.common.exception.exceptions;

import com.zoohee.auth.common.exception.ErrorCode;

public class PasswordNotMatchedException extends DefaultException {
    public PasswordNotMatchedException() {
        super(ErrorCode.PASSWORD_NOT_MATCHED.getMessage());
    }
}
