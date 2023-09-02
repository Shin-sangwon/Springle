package com.springle.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //Account
    DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다"),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다"),
    //Member
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다");

    private final HttpStatus httpStatus;
    private final String message;
}
