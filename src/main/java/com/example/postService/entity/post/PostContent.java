package com.example.postService.entity.post;

import com.example.postService.dto.post.resquest.UpdatePostRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_content")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class PostContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_content_id")
    private Long postContentId;

    @Column(nullable = false)
    private String text;//게시글 내용


    private String postImage;//게시름 이미지


    //내용 및 이미지 업데이트 메서드
    public void updatePostContent(UpdatePostRequestDto updatePostRequestDto) {
        this.text = updatePostRequestDto.getText();
        this.postImage = updatePostRequestDto.getPostImage();
    }
}

