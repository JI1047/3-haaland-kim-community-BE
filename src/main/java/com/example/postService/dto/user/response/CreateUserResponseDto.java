package com.example.postService.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserResponseDto {

    private String email;//사용자 이메일

    private String nickname;//사용자 닉네임

    private String profileImage;//사용자 프로필 이미지


}
/**
 * 회원가입 응답 dto
 * 사용자의 email, nickname, profileImage를 응답해줌
 */
