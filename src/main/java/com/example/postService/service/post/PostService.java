package com.example.postService.service.post;

import com.example.postService.dto.post.response.GetPostListResponseDto;
import com.example.postService.dto.post.response.GetPostResponseDto;
import com.example.postService.dto.post.resquest.CreatePostRequestDto;
import com.example.postService.dto.post.resquest.UpdatePostRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PostService {
    @Transactional
    ResponseEntity<String> createPost(CreatePostRequestDto dto, HttpServletRequest httpServletRequest);

    @Transactional
    ResponseEntity<String> updatePost(UpdatePostRequestDto dto, Long postId);

    @Transactional
    ResponseEntity<String> deletePost(Long postId);

    ResponseEntity<List<GetPostListResponseDto>> getPosts(int page, int size);

    ResponseEntity<GetPostResponseDto> getPost(Long postId);

    @Transactional
    ResponseEntity<String> updatePostLike(Long postId, HttpServletRequest httpServletRequest);
}
