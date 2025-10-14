package com.example.postService.entity.post;

import com.example.postService.dto.post.resquest.UpdatePostRequestDto;
import com.example.postService.entity.BaseTime;
import com.example.postService.entity.comment.Comment;
import com.example.postService.entity.user.UserProfile;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Post extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//id 자동 증가 전략
    private Long postId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)//좋아요,조회,댓글 수 엔터티 외래 키로 선언
    @JoinColumn(name = "post_view_id")
    private PostView postView;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)//내용,사진 엔터티 외래 키로 선언
    @JoinColumn(name = "post_content_id")
    private PostContent postContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")//사용자와 ManyToOne 매핑 외래 키 선언
    private UserProfile userProfile;

    @Column(nullable = false, length = 26)//제목 글자 수 제한/not null
    private String title;


    /**
     * cascade를 통해서 부모 저장 삭제 시 함께 저장/삭제
     * cascade만 사용하게 된다면 댓글 삭제 시 db에 그대로 남아있게됨(고아 현상)
     * 고아 객체 - 부모 엔터티와의 연관 관계가 끊어진 자식 엔터티
     * orphanRemoval = true를 통해서 고아 객체를 자동으로 DB에서 삭제
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default//Builder로 객체 생성시 null이 될 수도있다.
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostLike> likes = new ArrayList<>();

    public void updatePost(UpdatePostRequestDto dto) {
        this.title = dto.getTitle();
    }//제목 업데이트 메서드

}
