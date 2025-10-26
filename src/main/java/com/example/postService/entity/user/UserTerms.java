package com.example.postService.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_terms")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserTerms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean agreeTerms;//커뮤니티 이용약관 동의 여부 저장


    private boolean agreePrivacy;//개인정보 동의 여부 저장

    private LocalDateTime createdDate= LocalDateTime.now();
    //이용약관 동의 날짜

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
