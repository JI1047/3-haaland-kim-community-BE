package com.example.postService.user;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class UserTest {

    @PersistenceContext
    private EntityManager entityManager;

//    @Test
//    @Rollback(false)
//    public void createUserTest() {//회원 가입 테스트
//
//
//        UserExtra userExtra = new UserExtra("haaland","www.s3.url");
//        UserBasic userBasic = new UserBasic(userExtra,"kjs001004@naver.com","11234","11234");
//        entityManager.persist(userBasic);
//
//        entityManager.flush();
//        entityManager.clear();
//
//
//        UserBasic userBasic11 = entityManager.find(UserBasic.class,userBasic.getUserId());
//        System.out.println("Nickname: "+ userBasic11.getUser().getNickname());
//        System.out.println("Email: "+ userBasic11.getEmail());
//    }
//    @Test
//    @Rollback(false)
//    public void testCreatedAndUpdatedAt() {
//
//        UserExtra userExtra = new UserExtra("haaland","www.s3.url");
//        entityManager.persist(userExtra);
//        UserBasic userBasic = new UserBasic(userExtra,"kjs001004@naver.com","11234","11234");
//        entityManager.persist(userBasic);
//        entityManager.flush();
//        entityManager.clear();
//
//        UserBasic userBasic11 = entityManager.find(UserBasic.class,userBasic.getUserId());
//        System.out.println("처음 createdAt: "+userBasic11.getCreatedAt());
//        System.out.println("처음 UpdatedAt: "+userBasic11.getUpdatedAt());
//
//        userBasic11.setPassword("1234");
//        entityManager.flush();
//        entityManager.clear();
//
//        UserBasic userBasic12 = entityManager.find(UserBasic.class,userBasic.getUserId());
//        System.out.println("나중 createdAt: "+userBasic12.getCreatedAt());
//        System.out.println("나중 UpdatedAt: "+userBasic12.getUpdatedAt());
//    }
}
