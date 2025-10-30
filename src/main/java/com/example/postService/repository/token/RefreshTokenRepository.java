package com.example.postService.repository.token;

import com.example.postService.entity.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 메서드 이름만으로 쿼리를 생성 주어진 토큰이면서 아직 무효화되지 않은 레코드를 Optional로 조회
     */
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    /**
     * 특정 사용자 ID에 연결된 모든 리프레시 토큰을 일괄 삭제할 때 사용됨(재로그인 시 기존 토큰 무효화 등)
     */
    void deleteByUserId(Long userId);
}
