package com.example.postService.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//비밀번호 업데이트시 클라이언트에서 서버로 요청에 사용되는 dto
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserPasswordRequestDto {

    @NotBlank(message = "비밀번호는 필수 입력입니다.")//null+공백문자포함 금지
    @Size(min = 8 , max = 20)//비밀번호는 8자 이상 20자 이하
    private String newPassword;//새 비밀번호

    @NotBlank(message = "비밀번호는 필수 입력입니다.")//null+공백문자포함 금지
    @Size(min = 8 , max = 20)//비밀번호는 8자 이상 20자 이하
    private String confirmPassword;//새 비밀번호 확인


}
/**
 * 비밀번호 변경 시 요청 dto 새 비밀번호, 새 비밀번호 확인
 */