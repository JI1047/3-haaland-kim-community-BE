package com.example.postService.dto.comment.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentDto {

    private String text;
}
/**
 * 댓글 생성 요청 dto 내용을 전달받음
 */
/////