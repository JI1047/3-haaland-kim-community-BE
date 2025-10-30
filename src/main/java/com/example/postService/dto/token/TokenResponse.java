package com.example.postService.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TokenResponse
 * ---------------------------------------------------------
 * AccessToken / RefreshToken 발급 결과 DTO
 * - JWT 발급 시 Controller/Service 간 통신에 사용
 * - 쿠키 저장, 응답 반환 등에 공용으로 활용
 * ---------------------------------------------------------
 */
@Getter
@AllArgsConstructor
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;
}
