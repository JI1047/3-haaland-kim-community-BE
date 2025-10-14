package com.example.postService.dto.comment.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentDto {

    private String text;

}
/**
 * 댓글 수정 요청 dto 내용을 입력받음
 */
