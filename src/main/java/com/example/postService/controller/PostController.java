package com.example.postService.controller;

import com.example.postService.dto.post.response.GetPostListResponseDto;
import com.example.postService.dto.post.response.GetPostListResponseWrapperDto;
import com.example.postService.dto.post.response.GetPostResponseDto;
import com.example.postService.dto.post.resquest.CreatePostRequestDto;
import com.example.postService.dto.post.resquest.UpdatePostRequestDto;
import com.example.postService.scheduler.PostViewSchedulerService;
import com.example.postService.service.post.PostService;
import com.example.postService.util.FileStorage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FileStorage fileStorage;
    private final PostViewSchedulerService postViewSchedulerService;

    //게시물 목록 조회(list) controller
    @GetMapping("/list")
    public ResponseEntity<GetPostListResponseWrapperDto> getAllPosts(@RequestParam int page, @RequestParam int size) {
        return postService.getPosts(page, size);
    }

    //게시물 상세 조회 controller
    @GetMapping("/{postId}")
    public ResponseEntity<GetPostResponseDto> getPost(@PathVariable Long postId) {
        postViewSchedulerService.addViewToCache(postId);

        return postService.getPost(postId);

    }

    //게시물 작성 controller
    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody CreatePostRequestDto dto, HttpServletRequest httpServletRequest) {

        return postService.createPost(dto, httpServletRequest);
    }

    @GetMapping("/{postId}/check-writer")
    public ResponseEntity<Map<String, Boolean>> checkPost(@PathVariable Long postId, HttpServletRequest httpServletRequest) {
        return postService.checkWriter(postId, httpServletRequest);
    }
    //게시물 update controller
    @PutMapping("/{postId}/update")
    public ResponseEntity<String> updatePost(@RequestBody UpdatePostRequestDto dto, @PathVariable Long postId, HttpServletRequest httpServletRequest) {

        return postService.updatePost(dto, postId, httpServletRequest);
    }

    //게시물 삭제 controller
    @DeleteMapping("/{postId}/delete")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        return postService.deletePost(postId);
    }


    //게시물 좋아요 처리 controller
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId,HttpServletRequest httpServletRequest) {
        return postService.updatePostLike(postId,httpServletRequest);
    }

    /**
     * 이미지 업로드 controller
     */
    @PostMapping("/image")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("file") MultipartFile file) throws IOException {
        String imageUrl = fileStorage.storeFile(file);
        return ResponseEntity.ok(imageUrl);
    }
}
