package com.example.postService.repository.comment;

import com.example.postService.entity.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * CommentJpaRepository
 * -----------------------------------------------------------
 * 댓글(Comment) 엔티티에 대한 데이터 접근 계층
 * -----------------------------------------------------------
 * - findAllByPostId(Long postId, Pageable pageable)
 *   → 특정 게시글(postId)에 포함된 댓글을 페이징 형태로 조회
 * -----------------------------------------------------------
 */
public interface CommentJpaRepository extends JpaRepository<Comment,Long> {

    // 특정 게시글에 속한 댓글을 페이징으로 조회
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    Page<Comment> findAllByPostId(@Param("postId") Long postId, Pageable pageable);

}
