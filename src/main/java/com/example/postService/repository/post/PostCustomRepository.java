package com.example.postService.repository.post;

import com.example.postService.entity.comment.Comment;
import com.example.postService.entity.post.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * QueryDSL interface
 */
public interface PostCustomRepository {
    List<Post> findListPostQueryDSL(Pageable pageable);

    List<Comment> findListCommentQueryDSL(Post post);
}
