package com.example.postService.jwt;

import com.example.postService.dto.token.TokenResponse;
import com.example.postService.entity.token.RefreshToken;
import com.example.postService.entity.user.User;
import com.example.postService.repository.token.RefreshTokenRepository;
import com.example.postService.repository.user.UserJpaRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * TokenService
 * ---------------------------------------------------------
 * AccessToken / RefreshToken 생성, 검증, 저장, 갱신 책임 담당
 * - JwtProvider를 통해 토큰 생성 및 파싱
 * - RefreshToken을 DB에 관리 (유효기간, 폐기 여부 등)
 * ---------------------------------------------------------
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserJpaRepository userJpaRepository;

    private static final int ACCESS_TOKEN_EXPIRATION = 15 * 60; // 15분
    private static final int REFRESH_TOKEN_EXPIRATION = 14 * 24 * 3600; // 14일

    /**
     * AccessToken 재발급 (RefreshToken 검증 후)
     */
    @Transactional
    public TokenResponse refreshTokens(String refreshToken, HttpServletResponse response) {
        var parsedRefreshToken = jwtProvider.parse(refreshToken);

        RefreshToken entity = refreshTokenRepository.findByTokenAndRevokedFalse(refreshToken).orElse(null);
        if (entity == null || entity.getExpiresAt().isBefore(Instant.now())) {
            return null;
        }

        Long userId = Long.valueOf(parsedRefreshToken.getBody().getSubject());
        User user = userJpaRepository.findById(userId).orElse(null);
        if (user == null) return null;

        String newAccessToken = jwtProvider.createAccessToken(user.getUserId());
        return new TokenResponse(newAccessToken, refreshToken);
    }

    /**
     * AccessToken / RefreshToken 생성 및 저장
     */
    @Transactional
    public TokenResponse generateAndSaveTokens(User user) {
        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        RefreshToken refreshEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRATION))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshEntity);

        return new TokenResponse(accessToken, refreshToken);
    }
    @Transactional(readOnly = true)
    public boolean validateAccessToken(String token) {
        try {
            jwtProvider.parse(token); // Signature, Expiration 자동 검증
            return true;
        } catch (Exception e) {
            return false; // 변조, 만료 등 예외 발생 시 false 반환
        }
    }

    @Transactional(readOnly = true)
    public Long extractUserId(String token) {
        try {
            var jws = jwtProvider.parse(token);
            return Long.valueOf(jws.getBody().getSubject());
        } catch (Exception e) {
            return null;
        }
    }


}
