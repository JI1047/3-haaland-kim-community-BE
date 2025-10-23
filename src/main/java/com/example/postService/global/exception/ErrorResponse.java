package com.example.postService.global.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private final boolean success;

    private final String code;

    private final String message;

    private final int status;

    private final String path;
}
