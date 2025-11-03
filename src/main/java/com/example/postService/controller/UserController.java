package com.example.postService.controller;

import com.example.postService.dto.user.request.UpdateUserPasswordRequestDto;
import com.example.postService.jwt.CookieUtil;
import com.example.postService.jwt.JwtProvider;
import com.example.postService.repository.token.RefreshTokenRepository;
import com.example.postService.service.user.UserService;
import com.example.postService.dto.login.request.LoginRequestDto;
import com.example.postService.dto.user.request.CreateUserRequestDto;
import com.example.postService.dto.user.request.UpdateUserProfileRequestDto;
import com.example.postService.dto.user.response.CreateUserResponseDto;
import com.example.postService.dto.user.response.GetUserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CookieUtil cookieUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입 controller
    @PostMapping("/sign-up")
    public ResponseEntity<CreateUserResponseDto> signUp(@Valid @RequestBody CreateUserRequestDto dto) {
        return ResponseEntity.ok(userService.signUp(dto));
    }

    //로그인 controller
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDto dto, HttpServletResponse httpServletResponse) {


        //userService.login로직을 통해 로그인 및 세션 설정 수행
        return userService.login(dto, httpServletResponse);

    }

    //회원정보 controller
    @GetMapping()
    public ResponseEntity<GetUserResponseDto> get(HttpServletRequest httpServletRequest) {
        return userService.get(httpServletRequest);

    }

    //   회원정보 수정(PUT)/nickname,profileImage 수정
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateUserProfileRequestDto dto, HttpServletRequest httpServletRequest) {

        return userService.updateProfile(dto, httpServletRequest);
    }

    //비밀번호 변경 controller
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdateUserPasswordRequestDto dto, HttpServletRequest httpServletRequest) {
        return userService.updatePassword(dto, httpServletRequest);
    }


    //회원정보 수정 controller(soft-delete)
    @DeleteMapping()
    public ResponseEntity<String> delete(HttpServletRequest httpServletRequest) {
        return userService.softDelete(httpServletRequest);
    }


    /**
     * 로그아웃 시 세션 무효화 진행
     * 클라이언트 쿠키에 저장하는 방식을 채택했기 때문에
     * 클라이언트에 저장된 JSESSIONID쿠키도 삭제 후 logout성곰 메시지반환
     *
     */
    //로그아웃 controller
    @PutMapping("/log-out")
    public ResponseEntity<String> logout(HttpServletRequest httpServletRequest, HttpServletResponse response) {

        // 1. AccessToken에서 userId 추출
        Long userId = (Long) httpServletRequest.getAttribute("userId");


        // 2. 해당 userId의 RefreshToken 전부 무효화
        refreshTokenRepository.deleteByUser_UserId(userId);

        cookieUtil.clearCookies(response, "accessToken", "refreshToken");
        return ResponseEntity.ok("로그아웃 성공");
    }

    /**
     * 이미지 업로드 controller
     */
    @PostMapping("/profile/image")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("file") MultipartFile file) {
        String imageUrl = userService.uploadProfileImage(file);
        return ResponseEntity.ok(imageUrl);
    }


}
