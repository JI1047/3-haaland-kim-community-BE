package com.example.postService.jwt;

import com.example.postService.dto.token.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    /** 공통 쿠키 생성 로직 */
    public void addTokenCookie(HttpServletResponse response, String name, String value, Integer maxAge) {

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/");

        // 🔥 maxAge 있으면 Persistent / 없으면 Session 쿠키
        if (maxAge != null) {
            builder.maxAge(maxAge);
        }

        response.addHeader("Set-Cookie", builder.build().toString());
    }

    /** AccessToken + RefreshToken → 둘 다 Session Cookie 로 설정 */
    public void addTokenCookies(HttpServletResponse response, TokenResponse tokenResponse) {

        // 🔥 accessToken → 세션 쿠키
        addTokenCookie(response, "accessToken", tokenResponse.getAccessToken(), null);

        // 🔥 refreshToken → 세션 쿠키 (여기서 null이 핵심)
        addTokenCookie(response, "refreshToken", tokenResponse.getRefreshToken(), null);
    }

    public void clearCookies(HttpServletResponse response, String... names) {
        for (String name : names) {
            addTokenCookie(response, name, null, 0);
        }
    }
}
