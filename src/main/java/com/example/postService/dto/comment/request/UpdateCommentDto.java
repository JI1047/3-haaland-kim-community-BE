package com.example.postService.dto.comment.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * UpdateCommentDto
 * -----------------------------------------------------------
 * 댓글 수정 요청 시 클라이언트로부터 전달받는 데이터 객체
 * - text: 댓글 본문 내용
 * -----------------------------------------------------------
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentDto {

    private String text;

}

