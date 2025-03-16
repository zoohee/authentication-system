package com.zoohee.auth.common.exception.exceptions;

import com.zoohee.auth.common.exception.ErrorCode;

public class UserNotFoundException extends DefaultException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_EXISTS.getMessage());
    }
}
