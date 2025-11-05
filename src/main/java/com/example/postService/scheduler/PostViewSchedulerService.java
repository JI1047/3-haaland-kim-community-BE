package com.example.postService.scheduler;

import com.example.postService.repository.post.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 게시물 조회수(PostView.lookCount) 비동기 동기화 스케줄러
 * 개별 요청 시마다 DB에 바로 올리게되면 I/O 부하가 크기 때문에
 * 일정시간(10초) 동안 들어온 조회수를 메모리에 누적 저장한 뒤
 * 주기적으로 DB에 반영하는 방식으로 성능을 최적화
 */
@Service
@RequiredArgsConstructor
public class PostViewSchedulerService {

    private final PostJpaRepository postJpaRepository;


    /**
     *<게시물 Id, 요청 동안 들어온 조회수 누적>을 저장할 Map 객체 생성
     * 여러 요청 스레드가 동시에 같은 postId에 접근할 수 있기 때문에
     * thread-sage한 구조 필요 ConcurrentHasMap사용
     */
    private final Map<Long, Integer> viewCache = new ConcurrentHashMap<>();

    /**
     * 조회 요청 시 호출되는 메서드
     * 조회된 게시물 ID를 받아 해당 게시물의 조회수를 1증가 시킨다.
     * @param postId
     */
    public void addViewToCache(Long postId) {
        viewCache.merge(postId,1,Integer::sum);
    }

    /**
     * 누적된 조회수를 DB에 주기적으로 반영하는 스케줄러
     *
     * 10초마다 실행되며, 트랜잭션 단위로 안전하게 처리된다.
     * 각 postId에 해당하는 Post 엔티티를 조회한 뒤,
     * PostView의 lookCount를 누적 수치만큼 증가시킨다.
     *
     * JPA의 Dirty Checking을 통해 트랜잭션 종료 시 자동 flush된다.
     */
    @Scheduled(fixedRate = 10_000)
    @Transactional
    public void flushViewsToDatabase() {
        if(viewCache.isEmpty()) {
            return;
        }
        //Map을 순회하면서 해당 postId에 대한 그 동안의 조회수를 업데이트 후 DB에 저장
        viewCache.forEach((postId, count) -> {
            postJpaRepository.findById(postId).ifPresent(post -> {
                post.getPostView().lookCountUpdate(
                        post.getPostView().getLookCount() + count
                );
            });
        });
        // JPA의 dirty checking으로 자동 flush
        postJpaRepository.flush();

        // 반영 후 캐시 초기화
        viewCache.clear();

        System.out.println("[VIEW SYNC] 조회수 동기화 완료");
    }


}
