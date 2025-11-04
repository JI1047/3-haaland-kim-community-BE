package com.example.postService.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * JwtProvider
 * ---------------------------------------------------------
 * JWT 생성과 검증을 담당하는 핵심 컴포넌트.
 * - AccessToken, RefreshToken 각각의 TTL(Time To Live) 관리
 * - 서명키(Key) 초기화 및 검증
 * - io.jsonwebtoken(JJWT) 기반으로 서명 및 파싱 수행
 * ---------------------------------------------------------
 */
@Component
public class JwtProvider {

    /**
     *  HMAC-SHA256 알고리즘 기반 서명키 초기화
     * - Base64로 인코딩된 문자열을 디코딩해 Key 객체로 변환
     * - 최소 256bit(32자 이상) 키를 사용해야 HMAC-SHA256이 정상 동작함
     */
    private final Key key = Keys.hmacShaKeyFor(
            Base64.getDecoder().decode("YWRhcHRlcnphZGFwdGVyemFkYXB0ZXJ6YWRhcHRlcnphZGFwdGVyeg==") // adapterzadapterzadapterzadapterzadapterz
    );

    /**
     * Access Token 생성 메서드
     * ---------------------------------------------------------
     * - JWT의 sub(subject)에 userId를 넣어 해당 사용자를 식별
     * - Access Token은 클라이언트 요청 시 매번 헤더/쿠키로 전달되어 인증 수행
     * - 15분 TTL로 설정하여 보안성 강화
     *
     * @param userId: 인증된 사용자의 식별자
     * @return 서명된 Access Token (JWT 문자열)
     */
    public String createAccessToken(Long userId) {
        long accessTtlSec = 60 * 60;         // 1시간
        return Jwts.builder()
                .setSubject(String.valueOf(userId))// JWT subject에 userId 저장
                .setIssuedAt(new Date()) //토큰 발급 시간
                .setExpiration(Date.from(Instant.now().plusSeconds(accessTtlSec)))//만료
                .signWith(key, SignatureAlgorithm.HS256)//HAMC-SHA256 서명
                .compact();
    }

    /**
     * JWT 파싱 및 검증
     * ---------------------------------------------------------
     * - 토큰 문자열을 해석하여 Jws<Claims> 형태로 반환
     * - 내부적으로 서명(Signature) 검증 수행
     * - 만료(expired), 서명 불일치(SignatureException), 포맷 오류(MalformedJwtException)
     *   등은 예외로 던져짐 → 호출 측(JwtAuthFilter)에서 try-catch 처리
     *
     * @param jwt: 클라이언트로부터 전달받은 JWT 문자열
     * @return 검증된 JWT의 Claims(body 포함)
     */
    public Jws<Claims> parse(String jwt) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
    }

    /**
     * Refresh Token 생성 메서드
     * ---------------------------------------------------------
     * - AccessToken 만료 후 새로운 토큰을 재발급할 때 사용
     * - 토큰 타입(typ="refresh")을 명시하여 구분
     * - JTI(JWT ID)를 UUID로 설정해 중복 방지 및 추적 용이
     * - 14일 TTL로 장기 세션 유지 가능
     *
     * @param userId: 인증된 사용자의 식별자
     * @return 서명된 Refresh Token (JWT 문자열)
     */
    public String createRefreshToken(Long userId) {
        long refreshTtlSec = 14L * 24 * 3600;
        return Jwts.builder()
                .setSubject(String.valueOf(userId))// 사용자 식별자
                .claim("typ", "refresh")// 토큰 유형 지정
                .setId(UUID.randomUUID().toString())// JWT ID (토큰 고유값)
                .setIssuedAt(new Date())// 발급 시간
                .setExpiration(Date.from(Instant.now().plusSeconds(refreshTtlSec)))// 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)// 동일 서명키로 서명
                .compact();
    }
}


