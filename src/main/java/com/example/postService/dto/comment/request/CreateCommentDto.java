package com.example.postService.dto.comment.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CreateCommentDto
 * -----------------------------------------------------------
 * 댓글 등록 요청 시 클라이언트로부터 전달받는 데이터 객체
 * -----------------------------------------------------------
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentDto {

    private String text;
}