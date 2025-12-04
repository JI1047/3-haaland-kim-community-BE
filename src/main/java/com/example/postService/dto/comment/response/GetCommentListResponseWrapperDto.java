package com.example.postService.dto.comment.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * GetCommentListResponseWrapperDto
 * -----------------------------------------------------------
 * 댓글 목록 조회 응답을 감싸는 DTO
 * - comments: 현재 페이지에 포함된 댓글 리스트
 * - isLast: 다음 페이지가 존재하는지 여부 (무한 스크롤 지원용)
 * -----------------------------------------------------------
 * 프론트에서는 isLast를 활용해 추가 로드 여부를 제어함
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetCommentListResponseWrapperDto {
    private List<GetCommentResponseDto> comments;
    private boolean isLast;
}

