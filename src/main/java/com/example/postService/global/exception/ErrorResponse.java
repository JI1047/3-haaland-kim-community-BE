package com.example.postService.global.exception;

import com.example.postService.global.error.ErrorDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
public class ErrorResponse {

    private boolean success;
    private String code;
    private String message;
    private int status;
    private String path;

    private List<ErrorDetail> errors;
}

