package com.example.postService.entity.post;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_view")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PostView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//데이터베이스의 자동 증가 컬럼을 위해 IDENTITY방법 사용
    @Column(name = "post_view_id")//db에는 user_id로 저장됨
    private Long postViewId;


    @Column(nullable = false)
    @Builder.Default//Builder로 객체 생성시 null로 생성될 수 있음
    private Integer likeCount=0;//좋아요 수(생성 시에는 0으로 설정)

    @Column(nullable = false)
    @Builder.Default//Builder로 객체 생성시 null로 생성될 수 있음
    private Integer commentCount=0;//댓글 수(생성 시에는 0으로 설정)

    @Column(nullable = false)
    @Builder.Default//Builder로 객체 생성시 null로 생성될 수 있음
    private Integer lookCount=0;//조회 수(생성 시에는 0으로 설정)

    //게시물 좋아요 증가 메서드
    public void likeCountIncrease() {
        likeCount+=1;
    }
    //게시물 좋아요 감소 메서드
    public void likeCountDecrease() {
        likeCount-=1;
    }

    //게시물 댓글 증가 메서드
    public void commentCountIncrease() {

        commentCount += 1;
    }

    //게시물 댓글 감소 메서드
    public void commentCountDecrease() {

        commentCount -= 1;
    }
    //조회수 증가 메서드 (이건 구현하지 못했습니다.)
    public void lookCountUpdate() {
        lookCount+=1;
    }


}
