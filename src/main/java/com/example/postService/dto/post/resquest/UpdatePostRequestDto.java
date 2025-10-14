package com.example.postService.dto.post.resquest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePostRequestDto {

    private String title;

    private String text;

    private String postImage;
}
/**\
 * 게시글 수정시 요청 dto
 * 게시글 제목, 내용, 이미지를 요청받음
 */
