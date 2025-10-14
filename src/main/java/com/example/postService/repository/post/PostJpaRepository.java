package com.example.postService.repository.post;

import com.example.postService.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PostJpaRepository extends JpaRepository<Post,Long>, PostCustomRepository {
    Page<Post> findAll(Pageable pageable);
}
