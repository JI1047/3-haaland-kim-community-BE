package com.example.postService.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.example.postService") // ✅ 전역 적용
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest httpServletRequest) {

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
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(httpServletRequest.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    //커스텀 예외처리 발생 하고 싶을 시 customException작성
}
