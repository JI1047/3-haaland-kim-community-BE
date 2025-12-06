package com.example.postService.global.exception;

import com.example.postService.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());  // RuntimeException.message 에 ErrorCode 메시지 세팅
        this.errorCode = errorCode;
    }
}
