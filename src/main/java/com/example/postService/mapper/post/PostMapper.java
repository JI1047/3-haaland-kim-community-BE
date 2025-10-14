package com.example.postService.mapper.post;

import com.example.postService.dto.comment.response.GetCommentResponseDto;
import com.example.postService.dto.post.response.GetPostListResponseDto;
import com.example.postService.dto.post.response.GetPostResponseDto;
import com.example.postService.dto.post.resquest.CreatePostRequestDto;
import com.example.postService.entity.post.Post;
import com.example.postService.entity.post.PostContent;
import com.example.postService.entity.post.PostLike;
import com.example.postService.entity.post.PostView;
import com.example.postService.entity.user.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {


    //게시물 생성 요청 dto -> 게시물 내용 mapper

    PostContent postContentDtoToPostContent(CreatePostRequestDto dto);

    //게시물 생성 mapper
    Post toPost(CreatePostRequestDto dto, PostView postView, UserProfile userProfile, PostContent postContent);

    //게시물 목록 조회 mapper
    @Mapping(source = "postView.likeCount", target = "likeCount")
    @Mapping(source = "postView.commentCount", target = "commentCount")
    @Mapping(source = "postView.lookCount", target = "lookCount")
    GetPostListResponseDto toGetPostListResponseDto(Post post, PostView postView, UserProfile userProfile);


    //게시물 상세 조회 mapper
    @Mapping(source = "responseDtoList", target = "comments")
    GetPostResponseDto toGetPostResponseDto(Post post, PostContent postContent, PostView postView, UserProfile userProfile, List<GetCommentResponseDto> responseDtoList);


    //PostLike 생성 mapper
    @Mapping(target = "postLikeId", ignore = true)
    @Mapping(source = "post", target = "post")
    @Mapping(source = "userProfile", target = "userProfile")
    PostLike toPostLike(Post post, UserProfile userProfile);
}
