package com.example.postService.session;

import com.example.postService.dto.user.session.UserSession;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class SessionManager {
    private static final String SESSION_KEY = "user";
    private static final String COOKIE_NAME = "JSESSIONID";

    /** 세션 생성 */
    public void createSession(HttpServletRequest request, UserSession userSession) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_KEY, userSession);
        session.setMaxInactiveInterval(60 * 60); // 1시간 유지
    }

    /** 세션 조회 */
    public UserSession getSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (UserSession) session.getAttribute(SESSION_KEY);
    }

    /** 세션 만료 (로그아웃 시 호출) */
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 클라이언트 쿠키 삭제
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /** 세션이 존재하는지 여부 체크 */
    public boolean hasSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SESSION_KEY) != null;
    }
}
