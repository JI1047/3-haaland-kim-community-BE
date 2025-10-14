package com.example.postService.dto.login.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    private String email;
    private String password;
}
/**
 * 로그인 시 요청 dto
 * email,password 입력 데이터
 */