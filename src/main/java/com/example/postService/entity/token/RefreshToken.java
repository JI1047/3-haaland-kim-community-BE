package com.example.postService.entity.token;

import com.example.postService.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    private Instant expiresAt;//토큰 만료시간

    private boolean revoked;//토큰 만료 여부

    /**
     * RefreshToken은 인증 상태 유지용 데이터이기 때문에
     * User엔터티와 연결
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;







}
