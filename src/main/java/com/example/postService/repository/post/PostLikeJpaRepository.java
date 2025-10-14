package com.example.postService.repository.post;

import com.example.postService.entity.post.Post;
import com.example.postService.entity.post.PostLike;
import com.example.postService.entity.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeJpaRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUserProfile(Post post, UserProfile userProfile);

    boolean existsByPostAndUserProfile(Post post, UserProfile userProfile);//중복 좋아요 방지를 위한 쿼리

    void deleteByPostAndUserProfile(Post post, UserProfile userProfile);
}
