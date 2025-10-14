package com.example.postService.repository.post;

import com.example.postService.entity.post.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewJpaRepository extends JpaRepository<PostView,Long> {
}
