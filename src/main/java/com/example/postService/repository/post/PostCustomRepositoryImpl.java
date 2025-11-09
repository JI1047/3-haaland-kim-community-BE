package com.example.postService.repository.post;

import com.example.postService.entity.comment.Comment;
import com.example.postService.entity.comment.QComment;
import com.example.postService.entity.post.Post;
import com.example.postService.entity.post.QPost;
import com.example.postService.entity.post.QPostView;
import com.example.postService.entity.user.QUserProfile;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 게시글 조회 커스텀 조회 Repository
 */
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;

    QPost post = QPost.post;

    QUserProfile userProfile = QUserProfile.userProfile;

    QPostView postView = QPostView.postView;

    QComment comment = QComment.comment;

    /**
     * QueryDSL 기반 게시글 목록 페이징 조회
     * 1) post.userProfile, post.postView를 fetch join으로 미리 로딩
     *      -> Lazy Loading으로 인한 N+1문제 방지
     * 2) Pageable 객체를 이용해 offset,limit을 설정하여 페이징 처리
     * 3) 게시글 전체 개수 세는 퀄
     * 4) Page 객체로 반환 하여 return
     */
    @Override
    public Page<Post> findListPostQueryDSL(Pageable pageable) {//현재 페이지 정보
        List<Post> content = queryFactory//현재 페이지의 게시글 리스트
                .selectFrom(post)
                .leftJoin(post.userProfile, userProfile).fetchJoin()//매핑 객체 fetch join 적용
                .leftJoin(post.postView, postView).fetchJoin()//매핑 객체 fetch join 적용
                .offset(pageable.getOffset())//pageable 매개변수 통해 offset설정
                .limit(pageable.getPageSize())//pageable 매개변수 통해 limit설정
                .fetch();
        Long total = queryFactory//전체 게시글 개수
                .select(post.count())
                .from(post)
                .fetchOne();
        return new PageImpl<>(content, pageable, total);
    }



}
