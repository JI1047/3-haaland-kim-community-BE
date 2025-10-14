package com.example.postService.util;


import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderUtil {


    public static String encode(String password) {//사용자가 입력한 원본 비밀번호를 BCrypt 알고리즘으로 암호화
        return BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, password.toCharArray());
    }
    //.withDefaults() -> 기본 설정으로 bcrypt인코더 설정
    //.hashToString(BCrypt.MIN_COST, password.toCharArray()); -> 전달된 문자열을 bcrypt알고리즘으로 해싱
    // 연산 강도 설정 가능 연산 강도 높을 수록 보안성은 높아지지만 해싱이 늦어짐
    // 최소 연산 강도로 설정

    //평문 비밀번호와 암호화된 비밀번호를 비교 후 검증
    public static boolean matches(String password, String encodedPassword) {//비밀번호 일치 검증 메서드
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), encodedPassword.toCharArray());
        return result.verified;
    }
}

//Spring Security를 사용하지 않고(아직 안배워서) BCrypt라이브러리 build.gradle에 의존성 추가 후 진행
