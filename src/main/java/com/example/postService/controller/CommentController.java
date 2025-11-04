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

    @GetMapping()
    public ResponseEntity<GetCommentListResponseWrapperDto> getAllComments(@PathVariable Long postId, @RequestParam int page, @RequestParam int size) {
        return commentService.getComments(postId, page, size);
    }
    //댓글 등록 controller
    @PostMapping()
    public ResponseEntity<String> createComment(@PathVariable Long postId, @RequestBody CreateCommentDto dto, HttpServletRequest httpServletRequest) {
        return commentService.createComment(postId, dto, httpServletRequest);
    }


    //댓글 수정 controller
    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody UpdateCommentDto dto, HttpServletRequest httpServletRequest) {
        return commentService.updateComment(postId,commentId,dto,httpServletRequest);

    }

    //댓글 삭제 controller
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, HttpServletRequest httpServletRequest) {
        return commentService.deleteComment(postId,commentId,httpServletRequest);
    }


}
