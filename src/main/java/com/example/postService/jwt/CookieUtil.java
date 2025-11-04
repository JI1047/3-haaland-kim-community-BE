package com.example.postService.jwt;

import com.example.postService.dto.token.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private static final int ACCESS_TOKEN_EXPIRATION = 15 * 60; // 15분
    private static final int REFRESH_TOKEN_EXPIRATION = 14 * 24 * 3600; // 14일
    /**
     * 공통 쿠키 생성 로직
     */
     public void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);


     }

    /** AccessToken + RefreshToken 쿠키를 한번에 추가 */
     public void addTokenCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        addTokenCookie(response, "accessToken", tokenResponse.getAccessToken(), ACCESS_TOKEN_EXPIRATION);
        addTokenCookie(response, "refreshToken", tokenResponse.getRefreshToken(), REFRESH_TOKEN_EXPIRATION);
    }

     public void clearCookies(HttpServletResponse response, String... cookieNames) {
        for (String name : cookieNames) {
            addTokenCookie(response, name, null, 0);
        }
    }

}
