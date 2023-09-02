package com.springle.member.exception;

import com.springle.global.exception.ErrorCode;
import com.springle.global.exception.GlobalException;

public class MemberException extends GlobalException {

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
