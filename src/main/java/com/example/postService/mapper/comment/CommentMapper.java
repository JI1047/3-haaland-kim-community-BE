package com.example.postService.mapper.comment;

import com.example.postService.dto.comment.request.CreateCommentDto;
import com.example.postService.dto.comment.response.GetCommentResponseDto;
import com.example.postService.entity.comment.Comment;
import com.example.postService.entity.post.Post;
import com.example.postService.entity.user.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    //댓글 생성 dto 변화 mapper
    @Mapping(source = "post", target = "post")
    @Mapping(source = "userProfile",target = "userProfile")
    Comment toComment(CreateCommentDto dto, Post post, UserProfile userProfile);

    GetCommentResponseDto toGetCommentResponseDto(Comment comment, UserProfile userProfile);

}

