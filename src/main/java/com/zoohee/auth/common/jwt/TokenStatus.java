package com.zoohee.auth.common.jwt;

import lombok.Getter;

@Getter
public enum TokenStatus {
    VALID,
    EXPIRED,
    INVALID
}
