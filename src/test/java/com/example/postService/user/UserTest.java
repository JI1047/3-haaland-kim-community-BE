package com.example.postService.user;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Transactional
public class UserTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Rollback(false)
    public void createUserTest() {//회원 가입 테스트


    }
}
