package com.example.postService.dto.user.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {
    private Long userProfileId;

    private String nickname;

    private String profileImage;
}
/**
 * 로그인 후 세션에 저장되는 사용자 정보 객체 UserSession
 * 게시물에서 많이 사용되는 UserProfileId,nickname,profileImage를 포함
 * 게시물 작성,댓글 작성 수정 등 UserId를 일일히 요청에서 받지 않고
 * session을 통해서 요청에서 따로 추출할 수 잇도록 설계
 */
