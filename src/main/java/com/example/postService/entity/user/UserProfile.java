package com.example.postService.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity//엔터티 등록
@Table(name = "user_profile")//테이블명 설정
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 매개변수가 없는 기본 생성자 생성
@Getter//Lombok을 통한 get메서드 자동 생성
@Builder//엔터티는 불변이여야 하기 때문에 setter 없이 Builder만 사용하여 한번 생성 후 수정할 수 없는 구조로 유지
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//데이터베이스의 자동 증가 컬럼을 위해 IDENTITY방법 사용
    @Column(name = "user_profile_id")//
    private Long userProfileId;

    @Column(nullable = false, unique = true,length = 10)//NOT NULL,무결성(중복 x)설정,길이 조건 설정
    private String nickname; //사용자 닉네임

    @Column
    private String profileImage;//사용자 프로필 이미지

    //닉에임,프로필이미지 업데이트시 사용될 메서드
    public void updateProfile(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

}


//왜 @NoArgsConstructor는 PROTECTED인가(PUBLIC/PRIVATE)가 아니라??
// JPA는 엔터티를 기본 생성자를 통해 객체를 생성함 (기본 생성자가 없으면 No default constructor for entity 오류가 발생함)
//-> 기본 생성자가 명시되어잇어야함
//  왜 protected 인가?
// public으로 선언하게 되면개발자가 new로 엔터티를 직접 만들게 되면 JPA가 관리하지 않는 비영속 상태 엔터티가 만들어지기 떄문에
// 영속성 컨텍스트 관리에서 어려움을 겪을 수 있기 때문
//JPA가 내부적으로 객체를 만들 수 있지만 개발자가 new를 통해 만드는것을 막기위해