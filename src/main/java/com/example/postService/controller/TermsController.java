package com.example.postService.controller;

import com.example.postService.dto.user.terms.TermsAgreementDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/terms")
public class TermsController {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @GetMapping("/signup")
    public String signupPage(Model model) {
        // 여러 개가 들어있을 수도 있으니 첫 번째 주소만 사용
        String frontendUrl = allowedOrigins.split(",")[0];
        model.addAttribute("frontendUrl", frontendUrl);
        return "signup"; // signup.html (타임리프)
    }

}
