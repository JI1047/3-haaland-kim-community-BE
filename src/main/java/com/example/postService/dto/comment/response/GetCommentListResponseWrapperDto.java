package com.example.postService.dto.comment.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetCommentListResponseWrapperDto {
    private List<GetCommentResponseDto> comments;
    private boolean isLast;
}

