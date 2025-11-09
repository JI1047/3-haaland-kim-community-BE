package com.example.postService.controller;

import com.example.postService.dto.comment.request.CreateCommentDto;
import com.example.postService.dto.comment.request.UpdateCommentDto;
import com.example.postService.dto.comment.response.GetCommentListResponseWrapperDto;
import com.example.postService.dto.post.response.GetPostListResponseWrapperDto;
import com.example.postService.service.comment.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 목록 조회
     * -----------------------------------------------------------
     * - 특정 게시글(postId)에 등록된 댓글 목록을 페이지네이션 형태로 조회
     * - page, size를 통해 요청된 범위의 댓글 데이터만 반환
     * -----------------------------------------------------------
     */
    @GetMapping()
    public ResponseEntity<GetCommentListResponseWrapperDto> getAllComments(@PathVariable Long postId, @RequestParam int page, @RequestParam int size) {
        return commentService.getComments(postId, page, size);
    }

    /**
     * 댓글 등록
     * -----------------------------------------------------------
     * - 로그인된 사용자 정보를 HttpServletRequest에서 추출
     * - 전달받은 CreateCommentDto를 통해 새 댓글 생성
     * -----------------------------------------------------------
     */
    @PostMapping()
    public ResponseEntity<String> createComment(@PathVariable Long postId, @RequestBody CreateCommentDto dto, HttpServletRequest httpServletRequest) {
        return commentService.createComment(postId, dto, httpServletRequest);
    }


    /**
     * 댓글 수정
     * -----------------------------------------------------------
     * - 요청된 commentId에 해당하는 댓글 내용 수정
     * - UpdateCommentDto를 통해 변경할 내용 전달
     * -----------------------------------------------------------
     */    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody UpdateCommentDto dto, HttpServletRequest httpServletRequest) {
        return commentService.updateComment(postId,commentId,dto,httpServletRequest);

    }

    /**
     * 댓글 삭제
     * -----------------------------------------------------------
     * - 요청된 commentId의 댓글을 삭제
     * -----------------------------------------------------------
     */    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, HttpServletRequest httpServletRequest) {
        return commentService.deleteComment(postId,commentId,httpServletRequest);
    }


}
