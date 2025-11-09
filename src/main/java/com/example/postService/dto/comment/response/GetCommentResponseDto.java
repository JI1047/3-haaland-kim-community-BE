package com.example.postService.dto.comment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * GetCommentResponseDto
 * -----------------------------------------------------------
 * 단일 댓글 조회 응답 DTO
 * - 프론트엔드에 댓글 단위 데이터를 전달할 때 사용
 * -----------------------------------------------------------
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetCommentResponseDto {

    private Long commentId;//댓글 고유 ID

    private String nickname; //사용자 닉네임

    private String profileImage;//사용자 프로필 이미지


    private String text;//댓글 내용



}

