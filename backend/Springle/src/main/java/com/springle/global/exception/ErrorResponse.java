package com.springle.global.exception;

public record ErrorResponse(
    ErrorCode errorCode,
    String message
) {
    public ErrorResponse(GlobalException e) {
        this (
            e.getErrorCode(),
            e.getMessage()
        );
    }

}
