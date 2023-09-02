package com.springle.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> ExceptionHandler(GlobalException e) {
        log.error("error exception : '{}'", e.getMessage(), e);

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(new ErrorResponse(e));

    }
}
