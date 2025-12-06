package com.example.postService.global.exception;

import com.example.postService.global.error.ErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "com.example.postService")
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {

        List<ErrorDetail> errorDetails = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorDetail.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .code("VALIDATION_ERROR")
                .message("입력값이 유효하지 않습니다.") // 전체 메시지
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errors(errorDetails) //  상세 에러 리스트 추가
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {

        ErrorDetail detail = ErrorDetail.builder()
                .field(null) // 비즈니스 로직이라 특정 필드 없음
                .message(e.getMessage())
                .build();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .code("VALIDATION_ERROR")
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errors(List.of(detail))
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest httpServletRequest) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .code("SERVER_ERROR")
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(httpServletRequest.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
