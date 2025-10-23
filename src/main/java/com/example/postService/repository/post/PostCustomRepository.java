package com.example.postService.repository.post;

import com.example.postService.entity.comment.Comment;
import com.example.postService.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * QueryDSL interface
 */
public interface PostCustomRepository {
    Page<Post> findListPostQueryDSL(Pageable pageable);

}
