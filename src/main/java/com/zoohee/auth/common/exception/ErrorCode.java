package com.zoohee.auth.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SERVER_ERROR("서버에서 오류가 발생했습니다."),
    USERNAME_DUPLICATED("는 중복된 아이디입니다."),
    USER_NOT_EXISTS("회원 정보가 존재하지 않습니다."),
    PASSWORD_NOT_MATCHED("비밀번호가 틀렸습니다."),
    ACCESS_DENIED("접근 권한이 없습니다.");

    private final String message;
}
