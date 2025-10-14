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


    //댓글 응답 dto 변환 mapper
    @Mapping(source = "userProfile.nickname", target = "nickname")
    @Mapping(source = "userProfile.profileImage", target = "profileImage")
    @Mapping(source = "comment.text", target = "text")
    GetCommentResponseDto toGetCommentResponseDto(Comment comment, UserProfile userProfile);

    //댓글 생성 dto 변화 mapper
    @Mapping(source = "post", target = "post")
    @Mapping(source = "userProfile",target = "userProfile")
    Comment toComment(CreateCommentDto dto, Post post, UserProfile userProfile);


}

