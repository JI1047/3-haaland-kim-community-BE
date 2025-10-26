package com.example.postService.repository.user;

import com.example.postService.entity.user.UserTerms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermsJpaRepository extends JpaRepository<UserTerms,Long> {

}
