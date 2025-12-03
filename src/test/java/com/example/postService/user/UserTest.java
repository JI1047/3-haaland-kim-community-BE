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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserTest {

    @Autowired
    private MockMvc mockMvc;  // Spring MVC 환경 구성

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;  // 진짜 빈 대신 Mockito Mock

    @InjectMocks
    private UserController userController;  // Mock service를 가진 실제 Controller

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // @Mock, @InjectMocks 초기화
    }

    @Test
    @DisplayName("Post /sign-up 회원가입 성공")
    void SignUpSuccess() throws Exception {

        CreateUserRequestDto requestDto = CreateUserRequestDto.builder()
                .email("test1@email.com")
                .nickname("test1")
                .password("Kkkkk11@")
                .confirmPassword("Kkkkk11@")
                .profileImage("http:image.com")
                .termsAgreement(new TermsAgreementDto(true, true, LocalDateTime.now()))
                .build();

        CreateUserResponseDto responseDto = CreateUserResponseDto.builder()
                .email("test1@email.com")
                .nickname("test1")
                .profileImage("http:image.com")
                .build();

        Mockito.when(userService.signUp(any())).thenReturn(responseDto);

        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test1@email.com"))
                .andExpect(jsonPath("$.nickname").value("test1"))
                .andExpect(jsonPath("$.profileImage").value("http:image.com"));
    }
}
