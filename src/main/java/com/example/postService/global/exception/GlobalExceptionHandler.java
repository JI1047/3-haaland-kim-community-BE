package com.example.postService.global.exception;

import com.example.postService.global.error.ErrorCode;
import com.example.postService.global.error.ErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "com.example.postService")
public class GlobalExceptionHandler {

    // ✅ 1) DTO Validation 실패
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

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.VALIDATION_ERROR,
                request.getRequestURI(),
                errorDetails
        );

        return ResponseEntity
                .status(ErrorCode.VALIDATION_ERROR.getStatus())
                .body(response);
    }

    // ✅ 2) 비즈니스 로직 예외 (중복 체크 등)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e,
            HttpServletRequest request) {

        ErrorCode code = e.getErrorCode();

        ErrorResponse response = ErrorResponse.of(
                code,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(code.getStatus())
                .body(response);
    }

    // ✅ 3) 그 외 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e,
            HttpServletRequest request) {

        e.printStackTrace(); // 로그 남기기 (추후 log.error 로 변경 가능)

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.SERVER_ERROR,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.SERVER_ERROR.getStatus())
                .body(response);
    }
}
