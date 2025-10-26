package com.example.postService.dto.user.request;

import com.example.postService.dto.user.terms.TermsAgreementDto;
import com.example.postService.entity.user.UserProfile;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    회원가입시 클라이언트에서 서버로 요청에 사용되는 dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequestDto {

    @NotBlank(message = "이메일은 필수 입력입니다.")//null+공백문자포함 금지
    @Email(message = "이메일 형식이 올바르지 않습니다.")//이메일 형식 검사
    private String email;//입력받은 이메일

    @NotBlank(message = "닉네임은 필수 입력입니다.")//null+공백문자포함 금지
    @Size(min = 1 , max = 10)//닉네임은 1자 이상 10자 이하
    private String nickname;//입력받은 닉네임

    @NotBlank(message = "비밀번호는 필수 입력입니다.")//null+공백문자포함 금지
    @Size(min = 8 , max = 20)//비밀번호는 8자 이상 20자 이하
    private String password;//입력받은 비밀번호

    @NotBlank(message = "비밀번호는 필수 입력입니다.")//null+공백문자포함 금지
    @Size(min = 8 , max = 20)//비밀번호는 8자 이상 20자 이하
    private String confirmPassword;//입력받은 비밀번호 확인(DB에는 저장안됨)

    private String profileImage;//입력받은 s3.Url

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private TermsAgreementDto termsAgreement;//약관 request Dto
}
/**
 * 회원가입 요청 dto
 * 사용자 email,nickname,비밀번호,비밀번호 확인(db에 저장안됨 확인용도),프로필 사진 데이터를 요청받음
 */

