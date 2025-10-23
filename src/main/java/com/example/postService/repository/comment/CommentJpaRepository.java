package com.example.postService.repository.comment;

import com.example.postService.entity.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentJpaRepository extends JpaRepository<Comment,Long> {
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    Page<Comment> findAllByPostId(@Param("postId") Long postId, Pageable pageable);

}
