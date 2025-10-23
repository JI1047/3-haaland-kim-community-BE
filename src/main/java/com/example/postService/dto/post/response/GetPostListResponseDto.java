package com.example.postService.dto.post.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPostListResponseDto {

    private Long postId;

    private String title;

    private LocalDateTime createdAt;

    private Integer likeCount;

    private Integer commentCount;

    private Integer lookCount;

    private String nickname; //사용자 닉네임


    private String profileImage;//사용자 프로필 이미지
}
/**
 * 게시물 목록 조회 응답 dto
 * 게시물들의 제목,생성일자,좋아요 수, 댓글 수, 조회 수, 닉네임, 사용자 프로필 이미지를 응답해줌
 */
