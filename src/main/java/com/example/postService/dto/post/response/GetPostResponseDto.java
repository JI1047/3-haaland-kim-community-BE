package com.example.postService.dto.post.response;

import com.example.postService.dto.comment.response.GetCommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPostResponseDto {
    private String title;

    private String text;

    private String postImage;

    private LocalDateTime createdAt;

    private Integer likeCount;

    private Integer commentCount;

    private Integer lookCount;

    private String nickname; //사용자 닉네임

    private String profileImage;//사용자 프로필 이미지

    private List<GetCommentResponseDto> comments;

}
/**
 * 게시물 상세 조회 응답 dto
 * 제목,게시글 내용,이미지,생성 일자,좋아요 수, 댓글 수, 조회 수, 작성자 닉네임, 작성자 프로필이미지
 * 댓글들 목록 List를 응답해준다.
 */