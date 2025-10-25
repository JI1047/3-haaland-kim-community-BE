package com.example.postService.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ErrorResponse {

    private final boolean success;

    private final String code;

    private final String message;

    private final int status;

    private final String path;
}
