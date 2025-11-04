package com.example.postService.scheduler;

import com.example.postService.repository.post.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PostViewSchedulerService {

    private final PostJpaRepository postJpaRepository;


    //<게시물 Id, 요청 동안 들어온 조회수 누적>을 저장할 Map 객체 생성
    private final Map<Long, Integer> viewCache = new ConcurrentHashMap<>();

    public void addViewToCache(Long postId) {
        viewCache.merge(postId,1,Integer::sum);
    }


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
