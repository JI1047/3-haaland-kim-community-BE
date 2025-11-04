package com.example.postService.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * JwtProvider
     * - JWT 생성, 서명 검증, 만료 시간 관리, Claims 파싱 등의 핵심 로직을 수행하는 컴포넌트
     * - 필터는 인증을 직접 처리하지 않고 JwtProvider에 검증을 위임함으로써 역할을 명확히 분리
     * - Provider는 JJWT(io.jsonwebtoken) 기반으로 Access / Refresh 분리, 예외 유형별 커스텀 처리 권장
     */
    private final JwtProvider jwtProvider;

    /**
     * 인증 제외 경로 설정
     * - 로그인, 토큰 갱신, 에러 페이지 등은 토큰 검증이 불필요
     * - startsWith() 기반 매칭이므로 "/login/success" 같은 경로도 패스됨 → 정확한 경로 패턴 관리 필요
     * Spring 서버에 도달한 실제 경로를 의미
     */
    private static final String[] EXCLUDED_PATHS = {
            "/api/users/login",
            "/api/users/sign-up",
            "/api/jwt/validate"

    };

    /**
     * shouldNotFilter()
     * - 위에서 정의한 예외 경로는 필터 검증을 생략하도록 처리
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return Arrays.stream(EXCLUDED_PATHS).anyMatch(path::startsWith);
    }


    /**
     * doFilterInternal()
     * - 요청이 들어올 때마다 실행되는 필터 핵심 로직
     * - 1) "/" 또는 "/index" 접근 시 로그인 페이지 리다이렉트 처리
     * - 2) 헤더 또는 쿠키에서 JWT 추출
     * - 3) 토큰 없으면 그대로 다음 필터로 전달
     * - 4) 토큰 검증 실패 → 로그인 페이지 리다이렉트 or 401 반환
     * - 5) 검증 성공 → userId, role을 request attribute로 저장
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        boolean isIndex = isIndexRequest(request);
        Optional<String> token = extractToken(request);

        // 토큰이 없는 경우 (비로그인 상태 접근)
        if (token.isEmpty()) {
            if(isIndex){
                response.sendRedirect("/login");// 루트 접근 시 로그인 페이지로 이동
                return;
            }
            filterChain.doFilter(request, response);// 다음 필터로 그대로 전달
            return;
        }
        // 토큰 유효성 검증 실패 시 처리
        if (!validateAndSetAttributes(token.get(), request)) {
            if(isIndex){
                response.sendRedirect("/login");
            }else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return;
        }
        // 검증 성공 → 체인 계속 진행
        filterChain.doFilter(request, response);
    }

    /**
     * 토큰 추출
     * - 우선 순위: Authorization 헤더 → accessToken 쿠키
     */
    private Optional<String> extractToken(HttpServletRequest request) {
        return extractTokenFromHeader(request)
                .or(() -> extractTokenFromCookie(request));
    }

    /**
     * Authorization 헤더에서 토큰 추출
     * - Bearer 스킴 표준 형식: "Authorization: Bearer <token>"
     */
    private Optional<String> extractTokenFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }

    /**
     * accessToken 쿠키에서 토큰 추출
     * - 쿠키명은 JwtProvider 발급 시 이름과 반드시 동일해야 함
     * - 실제 운영 환경에서는 Secure, HttpOnly, SameSite=None 설정 필수
     */
    private Optional<String> extractTokenFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    /**
     * 인덱스 경로 접근 확인
     * - 루트("/") 또는 "/index"로 접근 시 true 반환
     */
    private boolean isIndexRequest(@NonNull HttpServletRequest request) {
        String uri = request.getRequestURI();
        return "/".equals(uri) || "index".equals(uri);
    }

    /**
     * 토큰 유효성 검증 및 요청 속성 세팅
     * - JwtProvider.parse() 호출 → Claims 추출
     * - 검증 성공 시 request에 사용자 식별값(userId)과 권한(role) 추가
     */
    private boolean validateAndSetAttributes(String token, HttpServletRequest request) {
        try {
            var jws = jwtProvider.parse(token);
            Claims body = jws.getBody();
            request.setAttribute("userId", Long.valueOf(body.getSubject()));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}