package com.example.postService.controller;

import com.example.postService.jwt.TokenService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
public class JwtController {

    private final TokenService tokenService;

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@CookieValue(value = "accessToken", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("login", false, "message", "토큰 없음"));
        }

        boolean valid = tokenService.validateAccessToken(token);
        if (!valid) {
            return ResponseEntity.status(401).body(Map.of("login", false, "message", "토큰 유효하지 않음"));
        }

        Long userId = tokenService.extractUserId(token);
        return ResponseEntity.ok(Map.of("login", true, "userId", userId));
    }
}
