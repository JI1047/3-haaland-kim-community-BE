package com.example.postService.service.comment.impl;

import com.example.postService.dto.comment.request.CreateCommentDto;
import com.example.postService.dto.comment.request.UpdateCommentDto;
import com.example.postService.dto.comment.response.GetCommentListResponseWrapperDto;
import com.example.postService.dto.comment.response.GetCommentResponseDto;
import com.example.postService.dto.user.session.UserSession;
import com.example.postService.entity.comment.Comment;
import com.example.postService.entity.post.Post;
import com.example.postService.entity.user.UserProfile;
import com.example.postService.mapper.comment.CommentMapper;
import com.example.postService.repository.comment.CommentJpaRepository;
import com.example.postService.repository.post.PostJpaRepository;
import com.example.postService.repository.user.UserProfileJpaRepository;
import com.example.postService.service.comment.CommentService;
import com.example.postService.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    private final CommentJpaRepository commentJpaRepository;

    private final PostJpaRepository postJpaRepository;

    private final UserProfileJpaRepository userProfileJpaRepository;

    private final SessionManager sessionManager;

    @Override
    public ResponseEntity<GetCommentListResponseWrapperDto> getComments(Long postId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Comment> comments = commentJpaRepository.findAllByPostId(postId, pageRequest);

        List<GetCommentResponseDto> responseDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            UserProfile userProfile = comment.getUserProfile();
            GetCommentResponseDto dto = commentMapper.toGetCommentResponseDto(comment, userProfile);
            responseDtoList.add(dto);
        }

        GetCommentListResponseWrapperDto wrapperDto =
                new GetCommentListResponseWrapperDto(responseDtoList, comments.isLast());

        return ResponseEntity.ok(wrapperDto);
    }

    /**
     * 댓글 생성 로직
     * 1. 파라미터로 받은 httpServletRequest을 통해 세션 객체 생성 세션이 없다고 자동생성을 방지하기 위해(false)로 선언
     * 2. 로그인을 해야 댓글 생성을 할 수 있도록 설계 했기 때문에 세션이 없을 시 로그인이 필요하다는 메세지 반환
     * 3, custom으로 생성한 UserSession생성 후 null이거 해당하는 User객체가 없을 시 사용자 정보를 찾을 수 없다고 반환
     * 4. UserSession의 UserProfileId로 UserProfile db로부터 조회(JPA 사용)
     * 5. 요청에 포함된 postId를 통해서 Post 조회
     * 5-1. 해당하는 Post가 없을시 에러 메세지 반환
     * 6.mapper을 통해서 Comment 객체 생성
     * 7. JPA통해 DB 저장
     * 8. 해당 Post의 댓글 수 증가
     * 9. 성공 메세지 반환
     *
     */
    @Transactional
    @Override
    public ResponseEntity<String> createComment(Long postId, CreateCommentDto dto, HttpServletRequest httpServletRequest) {



        UserSession userSession = sessionManager.getSession(httpServletRequest);

        if (userSession == null || userSession.getUserProfileId() == null) {
            throw new IllegalArgumentException("접근할 수 없습니다. 로그인 해주세요!");
        }

        UserProfile userProfile = userProfileJpaRepository.findById(userSession.getUserProfileId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 프로필을 찾을 수 없습니다."));


        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));


        Comment comment = commentMapper.toComment(dto, post, userProfile);

        commentJpaRepository.save(comment);

        post.getPostView().commentCountIncrease();

        return ResponseEntity.ok("댓글 생성 성공");

    }


    /**
     *댓글 수정 로직
     * 1. 파라미터로 받은 httpServletRequest을 통해 세션 객체 생성 세션이 없다고 자동생성을 방지하기 위해(false)로 선언
     * 2. 로그인을 해야 댓글 생성을 할 수 있도록 설계 했기 때문에 세션이 없을 시 로그인이 필요하다는 메세지 반환
     * 3, custom으로 생성한 UserSession생성 후 null이거 해당하는 User객체가 없을 시 세션 정보가 유효하지 않음을 반환
     * 4. UserSession의 UserProfileId로 UserProfile db로부터 조회(JPA 사용)
     * 5. 요청에 포함된 postId를 통해서 Post 조회
     * 5-1. 해당하는 Post가 없을시 에러 메세지 반환
     * 6. 요청에 포함된 commentId를 통해서 Comment 조회
     * 6-1. 해당하는 Comment가 없을시 에러 메세지 반환
     * 7. UserSession으로 조회한 UserProfile과 Comment의 작성자가 같은지 검증 진행
     * 7-1 해당하는 Comment의 작성자가 아닐시 에러 메세지 반환
     * 8. Comment update 메서드를 통해서 댓글 내용 업데이트
     * 9. 성공 메세지 반환
     */
    @Transactional
    @Override
    public ResponseEntity<String> updateComment(Long postId, Long commentId, UpdateCommentDto dto, HttpServletRequest httpServletRequest) {

        UserSession userSession = sessionManager.getSession(httpServletRequest);

        if (userSession == null || userSession.getUserProfileId() == null) {
            throw new IllegalArgumentException("접근할 수 없습니다. 로그인 해주세요!");
        }

        UserProfile userProfile = userProfileJpaRepository.findById(userSession.getUserProfileId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 프로필을 찾을 수 없습니다."));


        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));

        Comment comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));

        if(!comment.getUserProfile().equals(userProfile)) {
            throw new IllegalArgumentException("해당 댓글을 작성한 작성자만 수정할 수 있습니다!");
        }
        comment.updateText(dto.getText());

        return ResponseEntity.ok("댓글 수정 성공!");
    }

    /**
     *댓글 삭제 로직
     * 1. 파라미터로 받은 httpServletRequest을 통해 세션 객체 생성 세션이 없다고 자동생성을 방지하기 위해(false)로 선언
     * 2. 로그인을 해야 댓글 생성을 할 수 있도록 설계 했기 때문에 세션이 없을 시 로그인이 필요하다는 메세지 반환
     * 3, custom으로 생성한 UserSession생성 후 null이거 해당하는 User객체가 없을 시 세션 정보가 유효하지 않음을 반환
     * 4. UserSession의 UserProfileId로 UserProfile db로부터 조회(JPA 사용)
     * 5. 요청에 포함된 postId를 통해서 Post 조회
     * 5-1. 해당하는 Post가 없을시 에러 메세지 반환
     * 6. 요청에 포함된 commentId를 통해서 Comment 조회
     * 6-1. 해당하는 Comment가 없을시 에러 메세지 반환
     * 7. UserSession으로 조회한 UserProfile과 Comment의 작성자가 같은지 검증 진행
     * 7-1 해당하는 Comment의 작성자가 아닐시 에러 메세지 반환
     * 8. Comment JPA 메서드를 통해서 댓글 삭제
     * 9. 해당 게시물의 댓글 수 1 감소
     * 10. 성공 메세지 반환
     */
    @Transactional
    @Override
    public ResponseEntity<String> deleteComment(Long postId, Long commentId, HttpServletRequest httpServletRequest) {


        UserSession userSession = sessionManager.getSession(httpServletRequest);

        if (userSession == null || userSession.getUserProfileId() == null) {
            throw new IllegalArgumentException("접근 할 수 없습니다. 로그인 해주세요!");
        }

        UserProfile userProfile = userProfileJpaRepository.findById(userSession.getUserProfileId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 프로필을 찾을 수 없습니다."));


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

    @Override
    public ResponseEntity<Map<String, Boolean>> checkWriter(Long postId, Long commentId, HttpServletRequest httpServletRequest) {
        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));
        Comment comment= commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));
        UserSession userSession = sessionManager.getSession(httpServletRequest);

        boolean isOwner = userSession.getUserProfileId()
                .equals(comment.getUserProfile().getUserProfileId());
        if (!isOwner) {
            throw new IllegalArgumentException("로그인 한 사용자와 일치하지 않습니다.");
        }
        return ResponseEntity.ok(Map.of("match", true));

    }
}
