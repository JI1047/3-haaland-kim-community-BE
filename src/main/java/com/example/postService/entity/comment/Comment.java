package com.example.postService.entity.comment;

import com.example.postService.entity.BaseTime;
import com.example.postService.entity.post.Post;
import com.example.postService.entity.user.User;
import com.example.postService.entity.user.UserProfile;
import jakarta.persistence.*;
import lombok.*;

/**
 * Comment Entity
 * -----------------------------------------------------------
 * 댓글 정보를 관리하는 엔티티
 * - BaseTime을 상속받아 생성일/수정일 자동 관리
 * -----------------------------------------------------------
 * - commentId: 댓글 고유 식별자 (PK)
 * - text: 댓글 본문 내용
 * - userProfile: 작성자 정보 (UserProfile과 다대일 연관관계)
 * - post: 해당 댓글이 속한 게시글 정보 (Post와 다대일 연관관계)
 * -----------------------------------------------------------
 * 연관관계:
 * - N:1 (여러 댓글 → 하나의 게시글)
 * - N:1 (여러 댓글 → 하나의 작성자)
 * -----------------------------------------------------------
 * - updateText(): 댓글 내용 수정
 */
@Entity
@Table(name = "comment")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String text;//댓글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;//작성자 정보 조회를 위한 매핑

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;//게시물 정보 조회를 위한 매핑




    //댓글 내용 수정 업데이트 메서드
    public void updateText(String text) {
        this.text = text;
    }

}