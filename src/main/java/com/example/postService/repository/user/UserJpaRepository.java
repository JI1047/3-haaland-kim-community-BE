package com.example.postService.repository.user;

import com.example.postService.entity.user.User;
import com.example.postService.entity.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User,Long>, UserCustomRepository {
    //사용자의 이메일을 통해 User실제 객체가 존재하는 지 확인하기 위한 JPA 메서드
    Optional<User> findByEmail(String email);

    /**
     *  전역 처리 핸들러를 선언햇고 boolean으로 가야한다.
     *  존재 여부를 확인 때는 boolean
     *  객체 조회 이메일을 통해 사용자를 찾는다던지 이거는 Optional
     *  JPA는 메서드 이름으로 쿼리를 자동 생성한다
     *  findByEmail
     *  - 리턴 타입 : Optional<User>
     *  - 발생 쿼리문 :SELECT * FROM user WHERE email =?(쿼리문)
     *  - 실제 객체가 있는 지 확인
     *  existsByEmail
     *  - 리턴 타입 boolean
     *  - 발생 쿼리문 : SELECT COUNT(*) >0 FROM user WHERE email =?
     *  - 유저 존재 여부만 확인
     */

    //사용자의 이메일을 통해 중복된 이메일이 있는지 확인하기 위한 JPA 메서드
    boolean existsByEmail(String email);

    //사용자의 닉네임을 통해 중복된 닉네임이 있는지 확인하기 위한 JPA 메서드
    boolean existsByUserProfile_Nickname(String nickname); // ✅ 핵심 포인트


    Optional<User> findByUserProfile(UserProfile userProfile);

    Optional<UserProfile> findByUser(User user);
    Optional<User> findByUserId(Long userId);


}
