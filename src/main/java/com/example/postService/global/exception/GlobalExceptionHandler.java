package com.example.postService.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalAccessException e, HttpServletRequest httpServletRequest) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .code("VALIDATION_ERROR")
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(httpServletRequest.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    //이외의 에외처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest httpServletRequest) {
        ErrorResponse errorResponse =ErrorResponse.builder()
                .success(false)
                .code("SERVER_ERROR")
                .message("서버 내부 오류가 발생했습니다.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(httpServletRequest.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    //커스텀 예외처리 발생 하고 싶을 시 customException작성
}
