package com.example.postService.post;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class PostTest {

    @PersistenceContext
    private EntityManager entityManager;

//    @Test
//    @Rollback(false)
//    public void savePost() {
//        UserExtra userExtra = new UserExtra("haaland1","www.s3.url");
//        UserBasic userBasic = new UserBasic(userExtra,"kjs001004@gmail.com","112345","11234");
//        entityManager.persist(userBasic);
//
//        entityManager.flush();
//        entityManager.clear();
//
//        UserExtra findUserExtra = entityManager.find(UserExtra.class, userExtra.getId());
//
//
//        PostExtra postExtra = new PostExtra(0, 0, 0);
//        entityManager.persist(postExtra);
//
//        PostBasic postBasic = new PostBasic(postExtra,findUserExtra,"자기소개","용인사는 26살 홀란드 입니다.","www.s3.url");
//        entityManager.persist(postBasic);
//        entityManager.flush();
//
//        entityManager.clear();
//
//        PostBasic postBasic1 = entityManager.find(PostBasic.class,postBasic.getPostId());
//        System.out.println("Title: "+postBasic1.getTitle());
//        System.out.println("Author: "+postBasic1.getUser().getNickname());
//
//    }
}
