package com.example.postService.jwt;

import com.example.postService.entity.token.RefreshToken;
import com.example.postService.entity.user.User;
import com.example.postService.repository.token.RefreshTokenRepository;
import com.example.postService.repository.user.UserJpaRepository;
import com.example.postService.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * TokenService
 * ---------------------------------------------------------
 * AccessToken / RefreshToken 생성, 검증, 저장, 갱신 책임 담당
 * - JWT Provider를 이용해 토큰을 실제로 생성
 * - RefreshToken 엔티티를 DB에 관리 (유효기간, 폐기 여부 등)
 * ---------------------------------------------------------
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional
    public TokenResponse refreshTokens(String refreshToken, HttpServletResponse response) {
        var parsedRefreshToken = jwtProvider.parse(refreshToken);

        RefreshToken entity = refreshTokenRepository.findByTokenAndRevokedFalse(refreshToken).orElse(null);

        if (entity == null || entity.getExpiresAt().isBefore(Instant.now())) {
            return null;
        }

        Long userId = Long.valueOf(parsedRefreshToken.getBody().getSubject());
        User user = userJpaRepository.findById(userId).orElse(null);

        if (user == null) {
            return null;
        }

        // refresh token은 유지하고 access token만 새로 발급
        String newAccessToken = jwtProvider.createAccessToken(user.getUserId());

        // access token 쿠키만 갱신
        addTokenCookie(response, "accessToken", newAccessToken, ACCESS_TOKEN_EXPIRATION);

        return new TokenResponse(newAccessToken, refreshToken);
    }
    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken) {
        var parsedRefreshToken = jwtProvider.parse(refreshToken);

        // DB에서 유효한 RefreshToken 조회
        RefreshToken entity = refreshTokenRepository.findByTokenAndRevokedFalse(refreshToken)
                .orElse(null);

        if (entity == null || entity.getExpiresAt().isBefore(Instant.now())) {
            return null; // 만료 or 위조
        }

        Long userId = Long.valueOf(parsedRefreshToken.getBody().getSubject());
        User user = userJpaRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        // refreshToken은 그대로 유지, accessToken만 재발급
        String newAccessToken = jwtProvider.createAccessToken(user.getUserId());

        return new TokenResponse(newAccessToken, refreshToken);
    }
    public record TokenResponse(String accessToken, String refreshToken) { }

}
