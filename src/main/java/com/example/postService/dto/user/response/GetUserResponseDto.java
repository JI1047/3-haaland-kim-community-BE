package com.example.postService.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserResponseDto {
    //회원 조회시 서버에서 클라이언트로 반환되는 dto

    private String email;//사용자 이메일


    private String nickname; //사용자 닉네임


    private String profileImage;//사용자 프로필 이미지

    private LocalDateTime createdAt;//사용자 생성 일자

    private LocalDateTime updatedAt;//사용자 최종 수정 일자

}
/**
 * User정보 조회 시 응답 dto
 * 사용자의 이메일,닉네임,프로필 이미지,생성일자,최종 수정 일자를 응답해준다.
 */
