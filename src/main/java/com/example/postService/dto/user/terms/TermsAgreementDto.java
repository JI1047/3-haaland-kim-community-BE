package com.example.postService.dto.user.terms;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TermsAgreementDto {

    private boolean agreeTerms;//커뮤니티 규칙 약관 동의 여부

    private boolean agreePrivacy;//커뮤니티 개인정보 약관 동의 여부

    private LocalDateTime agreeTime;//커뮤니티 약관 동의 여부 날짜 및 시간
}
