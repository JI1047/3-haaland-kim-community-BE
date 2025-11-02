package com.example.postService.jwt;

import com.example.postService.dto.token.TokenResponse;
import com.example.postService.entity.token.RefreshToken;
import com.example.postService.entity.user.User;
import com.example.postService.repository.token.RefreshTokenRepository;
import com.example.postService.repository.user.UserJpaRepository;
import io.jsonwebtoken.ExpiredJwtException;
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
     * RTR 방식 (RefreshTokenRotation)
     * Refresh Token을 1회용으로 만들어서 ‘한 번 쓴 RefreshToken은 즉시 무효화하고 재사용이 감지되면 TokenFamily 전체를 무효화해 탈취를 차단하는 기법
     * TokenFamily - RT(1)가 무효화 되고 RT(2)가 탄생 같이 클라이언트에 대해 발행된 원본 RefreshToken으로부터 교환한 모든 Refresh Token
     * - 한번 사용된 RefreshToken은 더 이상 유효하지 않음
     * - 이상요청이나 로그아웃 시 패밀리 전체를 무효화
     * 1. AT만료되었을 때 기존 refreshToken 처럼 Refresh 토큰 및 토큰 해당 User 검증
     * 2. AccessToken 새로 발급
     * 3. 기존 RefreshToken revoked True로 변경
     * 4. 해당 User의 RefreshToken 새로 생성
     * 5. 새로운 AccessToken,RefreshToken 발급
     *
     */
    @Transactional
    public TokenResponse refreshTokensRTR(String refreshToken, HttpServletResponse response) {

        //RefreshToken 추출
        var parsedRefreshToken = jwtProvider.parse(refreshToken);

        //RefreshToken 검증 없거나 만료 시 null 반환
        RefreshToken entity = refreshTokenRepository.findByTokenAndRevokedFalse(refreshToken).orElse(null);
        if (entity == null || entity.getExpiresAt().isBefore(Instant.now())) {
            return null;
        }

        //RefreshToken의 userId를 추출하여 User 검증 없다면 null반환
        Long userId = Long.valueOf(parsedRefreshToken.getBody().getSubject());
        User user = userJpaRepository.findById(userId).orElse(null);
        if (user == null) return null;



        //해당 refreshToken의 revoked를 true로 변경하여 만료됨으로 업데이트
        entity.updateRevoked(true);

        //새로운 AccessToken/ RefreshToken 생성 및 저장(generateAndSaveTokens 메서드 재사용)
        return generateAndSaveTokens(user);
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
        }catch (ExpiredJwtException e) {
            System.out.println("AccessToken 만료됨");
            return false;
        }
        catch (Exception e) {
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
