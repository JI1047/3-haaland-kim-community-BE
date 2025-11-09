package com.example.postService.service.comment.impl;

import com.example.postService.dto.comment.request.CreateCommentDto;
import com.example.postService.dto.comment.request.UpdateCommentDto;
import com.example.postService.dto.comment.response.GetCommentListResponseWrapperDto;
import com.example.postService.dto.comment.response.GetCommentResponseDto;
import com.example.postService.dto.user.session.UserSession;
import com.example.postService.entity.comment.Comment;
import com.example.postService.entity.post.Post;
import com.example.postService.entity.user.User;
import com.example.postService.entity.user.UserProfile;
import com.example.postService.jwt.CookieUtil;
import com.example.postService.jwt.TokenService;
import com.example.postService.mapper.comment.CommentMapper;
import com.example.postService.repository.comment.CommentJpaRepository;
import com.example.postService.repository.post.PostJpaRepository;
import com.example.postService.repository.user.UserJpaRepository;
import com.example.postService.repository.user.UserProfileJpaRepository;
import com.example.postService.service.comment.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * CommentServiceImpl
 * -----------------------------------------------------------
 * 댓글 관련 비즈니스 로직을 처리하는 서비스 구현체
 * - 댓글 조회 / 등록 / 수정 / 삭제 로직 담당
 * - UserId를 HttpServletRequest attribute로 받아 인증 기반 처리
 * -----------------------------------------------------------
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final CommentJpaRepository commentJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final UserJpaRepository userJpaRepository;


    /**
     * 댓글 목록 조회 로직
     * -----------------------------------------------------------
     * 1. PathVariable로 전달된 postId를 기반으로 댓글 조회
     * 2. Pageable(page, size)을 생성하여 페이징 처리
     * 3. Comment 엔티티를 CommentMapper를 통해 DTO로 변환
     * 4. 마지막 페이지 여부(isLast) 포함한 래퍼 DTO 반환
     * -----------------------------------------------------------
     */
    @Override
    public ResponseEntity<GetCommentListResponseWrapperDto> getComments(Long postId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);// 페이지 요청 객체 생성

        Page<Comment> comments = commentJpaRepository.findAllByPostId(postId, pageRequest);// 댓글 조회

        List<GetCommentResponseDto> responseDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            UserProfile userProfile = comment.getUserProfile();// 작성자 프로필 조회
            GetCommentResponseDto dto = commentMapper.toGetCommentResponseDto(comment, userProfile);// DTO 변환
            responseDtoList.add(dto);
        }

        GetCommentListResponseWrapperDto wrapperDto =
                new GetCommentListResponseWrapperDto(responseDtoList, comments.isLast());// 마지막 페이지 여부 포함

        return ResponseEntity.ok(wrapperDto);
    }

    /**
     * 댓글 생성 로직
     * -----------------------------------------------------------
     * 인증/인가
     * - JWT 인증 필터에서 HttpServletRequest attribute("userId")로 사용자 ID를 주입받음
     * - 로그인하지 않은 사용자는 댓글 생성 불가
     * -----------------------------------------------------------
     * 처리 순서
     * 1) 요청 객체에서 userId 추출 → 비로그인 시 IllegalArgumentException
     * 2) userId로 User 조회 후 UserProfile 획득
     * 3) postId로 게시글(Post) 조회 → 없으면 예외
     * 4) CommentMapper를 통해 DTO → Entity 변환
     * 5) commentJpaRepository.save()로 DB 저장
     * 6) post.getPostView().commentCountIncrease()로 댓글 수 증가
     * 7) 성공 메시지 반환
     * -----------------------------------------------------------
     */
    @Transactional
    @Override
    public ResponseEntity<String> createComment(Long postId, CreateCommentDto dto, HttpServletRequest httpServletRequest) {



        Long userId = (Long) httpServletRequest.getAttribute("userId");// JWT 인증 시 필터에서 저장된 userId 사용

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        UserProfile userProfile = user.getUserProfile();


        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));

        Comment comment = commentMapper.toComment(dto, post, userProfile);// DTO → Entity 매핑

        commentJpaRepository.save(comment);

        post.getPostView().commentCountIncrease();// 게시글 댓글 수 +1

        return ResponseEntity.ok("댓글 생성 성공");

    }


    /**
     * 댓글 수정 로직
     * -----------------------------------------------------------
     * 인증/인가
     * - 로그인된 사용자만 수정 가능
     * - 댓글 작성자 본인인지 검증
     * -----------------------------------------------------------
     * 처리 순서
     * 1) 요청에서 userId 추출
     * 2) userId로 User 및 UserProfile 조회
     * 3) postId로 Post 조회 → 없으면 예외
     * 4) commentId로 Comment 조회 → 없으면 예외
     * 5) Comment 작성자(UserProfile)와 요청자 일치 여부 검증
     * 6) 일치 시 comment.updateText()로 내용 수정
     * 7) 성공 메시지 반환
     * -----------------------------------------------------------
     */
    @Transactional
    @Override
    public ResponseEntity<String> updateComment(Long postId, Long commentId, UpdateCommentDto dto, HttpServletRequest httpServletRequest) {

        Long userId = (Long) httpServletRequest.getAttribute("userId");// 인증된 사용자 ID 가져오기

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        UserProfile userProfile = user.getUserProfile();


        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));

        Comment comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        if(!comment.getUserProfile().equals(userProfile)) {// 작성자 검증
            throw new IllegalArgumentException("해당 댓글을 작성한 작성자만 수정할 수 있습니다!");
        }
        comment.updateText(dto.getText());// 댓글 내용 수정

        return ResponseEntity.ok("댓글 수정 성공!");
    }

    /**
     * 댓글 삭제 로직
     * -----------------------------------------------------------
     * 인증/인가
     * - JWT 인증 필터에서 HttpServletRequest attribute("userId")로 주입된 사용자 ID를 사용
     * - 댓글 작성자 본인만 삭제 가능
     * -----------------------------------------------------------
     * 처리 순서
     * 1) 요청 컨텍스트에서 userId 추출
     * 2) userId로 User 조회 → 없으면 예외
     * 3) User로부터 UserProfile 조회
     * 4) path 변수 postId로 Post 조회 → 없으면 예외
     * 5) path 변수 commentId로 Comment 조회 → 없으면 예외
     * 6) Comment.userProfile == 요청자 UserProfile 인지 검증 → 불일치 시 예외
     * 7) commentJpaRepository.delete(comment)로 삭제 수행
     * 8) post.getPostView().commentCountDecrease()로 댓글 수 감소
     * 9) "댓글 삭제 성공" 메시지와 함께 200 OK 반환
     * -----------------------------------------------------------
     */
    @Transactional
    @Override
    public ResponseEntity<String> deleteComment(Long postId, Long commentId, HttpServletRequest httpServletRequest) {


        Long userId = (Long) httpServletRequest.getAttribute("userId");

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        UserProfile userProfile = user.getUserProfile();


        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));

        Comment comment= commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        if(!comment.getUserProfile().equals(userProfile)) {
            throw new IllegalArgumentException("해당 댓글 작성자만 수정할 수 있습니다.");
        }

        commentJpaRepository.delete(comment);

        post.getPostView().commentCountDecrease();

        return ResponseEntity.ok("댓글 삭제 성공");
    }


}
