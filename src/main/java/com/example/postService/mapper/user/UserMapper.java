package com.example.postService.mapper.user;

import com.example.postService.dto.user.request.CreateUserRequestDto;
import com.example.postService.dto.user.response.CreateUserResponseDto;
import com.example.postService.dto.user.response.GetUserResponseDto;
import com.example.postService.dto.user.session.UserSession;
import com.example.postService.dto.user.terms.TermsAgreementDto;
import com.example.postService.entity.user.User;
import com.example.postService.entity.user.UserProfile;
import com.example.postService.entity.user.UserTerms;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper (componentModel = "spring")
public interface UserMapper {


    //UserProfile 생성 mapper
    UserProfile createUserRequestDtoToUserProfile(CreateUserRequestDto dto);

    //User 생성 mapper
    User createUserRequestDto(CreateUserRequestDto dto, UserProfile userProfile);

    //회원가입 이후 응답 dto mapper
    @Mapping(source = "userProfile.nickname", target = "nickname")
    @Mapping(source = "userProfile.profileImage", target = "profileImage")
    CreateUserResponseDto userToCreateUserResponseDto(User user);

    //회원 정보 조회 mapper
    @Mapping(source = "userProfile.nickname", target = "nickname")
    @Mapping(source = "userProfile.profileImage", target = "profileImage")
    GetUserResponseDto userToUGetUserResponseDto(User user);

    //UserSession 생성 mapper
    @Mapping(source = "userProfileId", target = "userProfileId")
    @Mapping(source = "nickname", target = "nickname")
    @Mapping(source = "profileImage",target = "profileImage")
    UserSession userProfileToSessionUser(UserProfile userProfile);

    @Mapping(target = "createdDate", expression = "java(java.time.LocalDateTime.now())")
    UserTerms TermsAgreementDtoToUserTerms(TermsAgreementDto dto,User user);

}
