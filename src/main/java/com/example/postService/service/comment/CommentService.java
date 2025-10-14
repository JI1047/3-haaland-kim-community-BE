package com.example.postService.service.comment;

import com.example.postService.dto.comment.request.CreateCommentDto;
import com.example.postService.dto.comment.request.UpdateCommentDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

public interface CommentService {
    //댓글 생성 로직
    @Transactional
    ResponseEntity<String> createComment(Long postId, CreateCommentDto dto, HttpServletRequest httpServletRequest);

    //댓글 수정 로직
    @Transactional
    ResponseEntity<String> updateComment(Long postId, Long commentId, UpdateCommentDto dto, HttpServletRequest httpServletRequest);

    @Transactional
    ResponseEntity<String> deleteComment(Long postId, Long commentId, HttpServletRequest httpServletRequest);
}
