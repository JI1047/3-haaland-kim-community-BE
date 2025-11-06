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

/**
 * 회원가입 요청 dto
 * 사용자 email,nickname,비밀번호,비밀번호 확인(db에 저장안됨 확인용도),프로필 사진,약관 동의여부 dto 데이터를 요청받음
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequestDto {

    /**
     * 회원가입시 요청받을 회원 이메일
     * 이메일은 null + 공백문자포함 금지로 설정
     * @Email 어노테이션을 통해 이메일 형식을 검사
     */
    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    /**
     * 회원가입 시 요청받을 회원 닉네임
     * 닉네임은 null + 공백 문자 포함 금지 설정
     * 닉네임은 1자 이상 10자 이하로 설정
     */
    @NotBlank(message = "닉네임은 필수 입력입니다.")
    @Size(min = 1, max = 10)
    private String nickname;

    /**
     * 회원가입 시 요청받을 회원 비밀번호
     * 비밀번호는 null+ 공백 문자 포함 금지 설정
     * 비밀번호는 8자 이상 20자 이하
     */
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 8, max = 20)
    private String password;

    /**
     * 회원가입시 요청받을 회원 비밀 번호 확인
     * 비밀번호확인은 DB에는 저장하지 않고 입력받은 비밀번호와 일치하는지 검증에 사용됨
     * null+공백문자포함 금지
     * 길이는 8자 이상 20자 이하로 설정
     */
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 8, max = 20)
    private String confirmPassword;

    /**
     * 회원가입시 요청 받을 회원 프로필 이미지 URL
     *
     */
    private String profileImage;

    /**
     * 회원가입시 요청 받을 약관 동의 dto
     * 약관 동의여부를 저장한 dto
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private TermsAgreementDto termsAgreement;
}


