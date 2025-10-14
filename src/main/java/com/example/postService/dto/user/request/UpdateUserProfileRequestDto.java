package com.example.postService.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//닉네임,프로필이미지 업데이트시 클라이언트에서 서버로 요청에 사용되는 dto
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileRequestDto {

    @NotBlank(message = "닉네임은 필수 입력입니다.")//null+공백문자포함 금지
    @Size(min = 1 , max = 10)//닉네임은 1자 이상 10자 이하
    private String nickname; //사용자 닉네임

    private String profileImage;//사용자 프로필 이미지


}
/**
 * 프로필 업데이트 요청 dto
 * 사용자의 닉네임, 프로필 이미지를 요청받음
 */
