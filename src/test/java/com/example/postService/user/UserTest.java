package com.example.postService.user;

import com.example.postService.controller.UserController;
import com.example.postService.dto.user.request.CreateUserRequestDto;
import com.example.postService.dto.user.response.CreateUserResponseDto;
import com.example.postService.dto.user.terms.TermsAgreementDto;
import com.example.postService.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserTest {

    @Mock
    private UserService userService;  // DB 접근 없음

    @InjectMocks
    private UserController userController;  // 직접 생성

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("회원가입 성공 - 순수 Mockito Controller Test")
    void signupSuccess() {

        CreateUserRequestDto req = CreateUserRequestDto.builder()
                .email("test1@email.com")
                .nickname("test1")
                .password("Kkkkk11@")
                .confirmPassword("Kkkkk11@")
                .profileImage("http:image.com")
                .termsAgreement(new TermsAgreementDto(true, true, LocalDateTime.now()))
                .build();

        CreateUserResponseDto res = CreateUserResponseDto.builder()
                .email("test1@email.com")
                .nickname("test1")
                .profileImage("http:image.com")
                .build();

        when(userService.signUp(any())).thenReturn(res);

        ResponseEntity<CreateUserResponseDto> response = userController.signUp(req);

        CreateUserResponseDto result = response.getBody();

        assertEquals("test1@email.com", result.getEmail());
        assertEquals("test1", result.getNickname());
        assertEquals("http:image.com", result.getProfileImage());

        verify(userService, times(1)).signUp(any());
    }
}
