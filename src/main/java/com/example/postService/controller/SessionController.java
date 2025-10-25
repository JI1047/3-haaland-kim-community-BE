package com.example.postService.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @GetMapping("/check")
    public ResponseEntity<?> checkSession(HttpSession session) {
        Object user = session.getAttribute("user"); // 세션에서 유저 확인
        if (user != null) {
            return ResponseEntity.ok(Map.of("login", true, "user", user));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("login", false));
        }
    }
}
