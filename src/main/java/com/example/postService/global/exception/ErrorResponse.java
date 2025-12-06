package com.example.postService.global.exception;

import com.example.postService.global.error.ErrorCode;
import com.example.postService.global.error.ErrorDetail;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ErrorResponse {

    private boolean success;
    private String code;      // ErrorCode 이름 (예: EMAIL_ALREADY_EXISTS, VALIDATION_ERROR)
    private String message;   // 사용자에게 보여줄 메시지
    private int status;       // HTTP 상태 코드
    private String path;      // 요청 URL
    private List<ErrorDetail> errors;  // 필드 단위 Validation 에러들 (없을 수도 있음)

    public static ErrorResponse of(ErrorCode errorCode, String path, List<ErrorDetail> errors) {
        return ErrorResponse.builder()
                .success(false)
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .status(errorCode.getStatus().value())
                .path(path)
                .errors(errors)
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return of(errorCode, path, null);
    }
}
