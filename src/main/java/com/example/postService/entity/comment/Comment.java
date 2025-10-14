package com.example.postService.entity.comment;

import com.example.postService.entity.BaseTime;
import com.example.postService.entity.post.Post;
import com.example.postService.entity.user.User;
import com.example.postService.entity.user.UserProfile;
import jakarta.persistence.*;
import lombok.*;

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