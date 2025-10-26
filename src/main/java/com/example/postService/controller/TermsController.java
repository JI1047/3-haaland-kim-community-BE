package com.example.postService.controller;

import com.example.postService.dto.user.terms.TermsAgreementDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/terms")
public class TermsController {

    @GetMapping()
    public String showTermsPage(Model model) {
        model.addAttribute("termsAgreement", new TermsAgreementDto());
        return "termsView";
    }

}
