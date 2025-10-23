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

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;

    QPost post = QPost.post;

    QUserProfile userProfile = QUserProfile.userProfile;

    QPostView postView = QPostView.postView;

    QComment comment = QComment.comment;

    /**
     *
     * Pageable 관련 객체를 받아 그만큼의 Post객체 list를 불러오도록하는 QueryDSL
     * fetch join을 이용해 lazy로 설정된 매핑 객체들을 id만 불러오는 것이아닌
     * 데이터들을 모두 미리 불러와 N+1문제 미리방지
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
