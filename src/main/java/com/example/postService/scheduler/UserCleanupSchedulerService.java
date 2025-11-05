package com.example.postService.scheduler;

import com.example.postService.repository.user.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 매일 자정에 soft_delete된 사용자 계정을 실제 DB에서 삭제하는 스케줄러
 * <p>
 * - 회원 탈퇴 시 is_deleted = true, deleted_at = 현재시간 으로 업데이트 됨
 * - 이후 스케줄러가 주기적으로 실행되어 실제 delete 처리
 */
@Service
@RequiredArgsConstructor
public class UserCleanupSchedulerService {

    private final UserJpaRepository userJpaRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void deleteSoftDeletedUsers() {
        try {
            int deletedUserCount = userJpaRepository.deleteByIsDeletedTrue();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
