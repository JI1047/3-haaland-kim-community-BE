package com.example.postService.repository.user;

import com.example.postService.entity.user.User;
import com.example.postService.entity.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileJpaRepository extends JpaRepository<UserProfile, Long> {


}
