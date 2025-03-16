package com.zoohee.auth.common.exception.exceptions;

import com.zoohee.auth.common.exception.ErrorCode;

public class UsernameDuplicatedException extends DefaultException {
    public UsernameDuplicatedException(String username) {
        super(username + ErrorCode.USERNAME_DUPLICATED.getMessage());
    }
}
