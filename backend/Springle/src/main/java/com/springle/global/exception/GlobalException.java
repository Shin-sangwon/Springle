package com.springle.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class GlobalException extends RuntimeException{

    private ErrorCode errorCode;
}
