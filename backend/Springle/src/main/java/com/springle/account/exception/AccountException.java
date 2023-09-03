package com.springle.account.exception;

import com.springle.global.exception.ErrorCode;
import com.springle.global.exception.GlobalException;


public class AccountException extends GlobalException {

    public AccountException(ErrorCode errorCode) {
        super(errorCode);
    }
}
