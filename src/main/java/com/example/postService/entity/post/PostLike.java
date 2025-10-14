package com.example.postService.entity.post;

import com.example.postService.entity.user.UserProfile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_like")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_id")
    private Long postLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;//좋아요 누른 user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;//좋아요 누른 게시물
}
/**
 * 게시글 좋아요 엔터티로 userProfile과 Post를 통해 관리
 */
