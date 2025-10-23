package com.example.postService.dto.post.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPostListResponseWrapperDto {
    private List<GetPostListResponseDto> posts;//게시글 목록
    private boolean hasMore;//마지막 페이지 여부
}
