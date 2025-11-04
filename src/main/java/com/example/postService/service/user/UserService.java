package com.example.postService.service.user;

import com.example.postService.dto.login.request.LoginRequestDto;
import com.example.postService.dto.user.request.CreateUserRequestDto;
import com.example.postService.dto.user.request.UpdateUserPasswordRequestDto;
import com.example.postService.dto.user.request.UpdateUserProfileRequestDto;
import com.example.postService.dto.user.response.CreateUserResponseDto;
import com.example.postService.dto.user.response.GetUserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    //회원가입을 처리하는 service
    @Transactional
    CreateUserResponseDto signUp(CreateUserRequestDto dto);

    //로그인 처리 Service 로직
    ResponseEntity<Map<String, Object>> login(LoginRequestDto dto, HttpServletResponse httpServletResponse);

    //회원 정보 조회 Service 로직
    ResponseEntity<GetUserResponseDto> get(HttpServletRequest httpServletRequest);

    //닉네임,프로필 이미지 수정 Service 로직
    ResponseEntity<String> updateProfile(UpdateUserProfileRequestDto dto, HttpServletRequest httpServletRequest);

    ResponseEntity<String> updatePassword(UpdateUserPasswordRequestDto dto, HttpServletRequest httpServletRequest);

    ResponseEntity<String> softDelete(HttpServletRequest httpServletRequest);


}
