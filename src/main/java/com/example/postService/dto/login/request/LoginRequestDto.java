package com.example.postService.dto.login.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 시 요청 dto
 * email, password 입력 데이터
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    private String password;
}
