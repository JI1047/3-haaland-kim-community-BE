package com.example.postService.controller;

import com.example.postService.entity.user.User;
import com.example.postService.jwt.CookieUtil;
import com.example.postService.jwt.TokenService;
import com.example.postService.repository.user.UserJpaRepository;
import com.example.postService.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
public class JwtController {

    private final TokenService tokenService;
    private final CookieUtil cookieUtil;
    private final UserJpaRepository userJpaRepository;

    /**
     * AccessToken & RefreshToken 검증 및 재발급 로깆
     * FE에 header.js 공통로직이(모든 페이지에 적용) 있습니다
     * jwt를 통해서 로그인 유무를 판단하고 그에 따른 버튼 노출에 차이를 두기 위해
     * header.js에서 /api/jwt/validate API를 호출하여 jwt 검증을 진행합니다.
     * 여기서 추가로 accessToken의 만료 시점을 알기 위해서
     * RefreshToken을 요청을 같이 받아 검증을 진행합니다.
     * ----------------------------------------------------------------
     * case 1. accessToken이 없는데 refreshToken이 있는경우
     *  이 경우는 로그인을 했는데 accessToken의 존재하지 않아
     *  accessToken을 재발급 받아야하는 상황입니다.
     *  accessToken 재발급 메서드를 호출하고 return합니다.
     * ----------------------------------------------------------------
     * case 2. accessToken이 없는데 refreshToken이 없는경우
     * 이 경우는 로그인을 진행하지않았거나 로그아웃한 상태입니다.
     * 로그인을 진행해야한다고 response에 message를 추가합니다.
     * ----------------------------------------------------------------
     * case 3. accessToken이 만료되었는데 refreshToken이 있는 경우
     * 이 경우도 로그인을 햇는데 accessToken이 만료되어
     * accessToken을 재발급 받아야하는 상황입니다.
     * accessToken 재발급 메서드를 호출하고 return합니다.
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpServletResponse
    ) {
        // accessToken이 없는 경우
        if (accessToken == null) {
            if (refreshToken != null) {
                var tokenResponse = tokenService.refreshTokensRTR(refreshToken, httpServletResponse);
                cookieUtil.addTokenCookies(httpServletResponse, tokenResponse);

                Long userId = tokenService.extractUserId(tokenResponse.getAccessToken());
                return ResponseEntity.ok(buildUserResponse(userId));

            }

            return ResponseEntity.status(401).body(Map.of(
                    "login", false,
                    "message", "로그인이 필요합니다.",
                    "canRefresh", false
            ));
        }

        // accessToken이 있지만 만료된 경우
        boolean valid = tokenService.validateAccessToken(accessToken);
        if (!valid) {
            if (refreshToken != null) {
                var tokenResponse = tokenService.refreshTokensRTR(refreshToken, httpServletResponse);
                cookieUtil.addTokenCookies(httpServletResponse, tokenResponse);

                Long userId = tokenService.extractUserId(tokenResponse.getAccessToken());
                return ResponseEntity.ok(buildUserResponse(userId));

            }

            return ResponseEntity.status(401).body(Map.of(
                    "login", false,
                    "message", "로그인이 필요합니다.",
                    "canRefresh", false
            ));
        }

        // accessToken 유효한 경우
        Long userId = tokenService.extractUserId(accessToken);
        return ResponseEntity.ok(buildUserResponse(userId));

    }
    //공통 응답 조립 메서드
    private Map<String, Object> buildUserResponse(Long userId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        Map<String, Object> result = new HashMap<>();
        result.put("login", true);
        result.put("userId", userId);
        result.put("nickname", user.getUserProfile().getNickname());
        result.put("profileImage", user.getUserProfile().getProfileImage());
        return result;
    }
}
