package com.example.postService.repository.comment;

import com.example.postService.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment,Long> {
}
