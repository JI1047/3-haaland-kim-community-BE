package com.example.postService.entity.user;

import com.example.postService.entity.BaseTime;
import com.example.postService.entity.token.RefreshToken;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user")//db 테이블명 설정
@EntityListeners(AuditingEntityListener.class)//BaseTime의 createdAt,updatedAt 자동 갱신을 위한 설정
@Getter
@Builder
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//데이터베이스의 자동 증가 컬럼을 위해 IDENTITY방법 사용
    @Column
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //OneToOne으로 설정된 UserProfile객체를 지연로딩을 통해서 불필요한 sql 쿼리를 줄이고
    // cascade을 ALL로 설정해 User 변동시 UserProfile도 동시에 변동 되도록 설정
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    @Column(nullable = false, unique = true, length = 150)//NOT NULL,무결성(중복 x)설정,길이 조건 설정
    private String email;

    @Column(nullable = false)
    private String password;

    @Transient
    private String confirmPassword;
    //비밀번호 확인 값 애플리케이션 내부 로직에서만 필요한 값

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    @Builder.Default//Builder로 객체 생성시 null로 생성될 수 있음
    private Boolean isDeleted = false;//boolean형은 기본 default값이 false이기 때문에 설정 x


    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private UserTerms userTerms;

    //패스워드 업데이트 메서드
    public void updatePassword(String password) {
        this.password = password;
    }

    //Soft_delete 업데이트 메서드
    public void updateDeleted() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }


}
//사용자의 닉네임 or 프로필 이미지를 통해서 이메일이나,패스워드를 조회하는 일은 거의 없기 때문에 단방향으로 설정함