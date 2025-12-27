package com.example.postService.controller;

import com.example.postService.dto.user.terms.TermsAgreementDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/terms")
public class TermsController {

    // cors.allowed-origins 대신 전용 설정값을 읽어옵니다.
    @Value("${app.frontend-url}")
    private String frontendUrl;

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("frontendUrl", frontendUrl);
        return "termsView";
    }

}
