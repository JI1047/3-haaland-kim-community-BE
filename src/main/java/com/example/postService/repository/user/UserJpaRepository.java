package com.example.postService.repository.user;

import com.example.postService.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User,Long>, UserCustomRepository {
    //사용자의 이메일을 통해 User을 조회하기 위한 JPA 메서드
    Optional<User> findByEmail(String email);//찾는 사용자가 null일 경우 예외처리 필요하기 때문에 Optional로 선언

}
