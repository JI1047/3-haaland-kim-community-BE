package com.example.postService.service.comment.impl;

import com.example.postService.dto.comment.request.CreateCommentDto;
import com.example.postService.dto.comment.request.UpdateCommentDto;
import com.example.postService.dto.user.session.UserSession;
import com.example.postService.entity.comment.Comment;
import com.example.postService.entity.post.Post;
import com.example.postService.entity.user.UserProfile;
import com.example.postService.mapper.comment.CommentMapper;
import com.example.postService.repository.comment.CommentJpaRepository;
import com.example.postService.repository.post.PostJpaRepository;
import com.example.postService.repository.user.UserProfileJpaRepository;
import com.example.postService.service.comment.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    private final CommentJpaRepository commentJpaRepository;

    private final PostJpaRepository postJpaRepository;

    private final UserProfileJpaRepository userProfileJpaRepository;


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

        HttpSession httpSession = httpServletRequest.getSession(false);

        if (httpSession == null) {
            return ResponseEntity.badRequest().body("로그인이 필요합니다.");
        }

        UserSession userSession = (UserSession) httpSession.getAttribute("user");

        if (userSession == null || userSession.getUserProfileId() == null) {
            return ResponseEntity.badRequest().body("해당 사용자 정보를 찾을 수 없습니다.");
        }

        Optional<UserProfile> userProfileOptional = userProfileJpaRepository.findById(userSession.getUserProfileId());


        Optional<Post> postOptional = postJpaRepository.findById(postId);
        if (postOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 게시물을 찾을 수 없습니다.");
        }

        UserProfile userProfile = userProfileOptional.get();

        Post post = postOptional.get();


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

        HttpSession httpSession = httpServletRequest.getSession(false);

        if (httpSession == null) {
            return ResponseEntity.badRequest().body("로그인이 필요합니다.");
        }

        UserSession userSession = (UserSession) httpSession.getAttribute("user");

        if (userSession == null || userSession.getUserProfileId() == null) {
            return ResponseEntity.badRequest().body("해당 사용자 정보를 찾을 수 없습니다.");
        }

        Optional<UserProfile> userProfileOptional = userProfileJpaRepository.findById(userSession.getUserProfileId());

        Optional<Post> postOptional = postJpaRepository.findById(postId);

        if (postOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 게시물을 찾을 수 없습니다.");
        }

        Optional<Comment> commentOptional = commentJpaRepository.findById(commentId);

        if (commentOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 댓글을 찾을 수 없습니다.");
        }
        UserProfile userProfile = userProfileOptional.get();
        Comment comment = commentOptional.get();

        if(!comment.getUserProfile().getUserProfileId().equals(userProfile.getUserProfileId())) {
            return ResponseEntity.badRequest().body("작성하신 사용자와 일치하지 않는 사용자입니다.");
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
        HttpSession httpSession = httpServletRequest.getSession(false);

        if (httpSession == null) {
            return ResponseEntity.badRequest().body("로그인이 필요합니다.");
        }

        UserSession userSession = (UserSession) httpSession.getAttribute("user");

        if (userSession == null || userSession.getUserProfileId() == null) {
            return ResponseEntity.badRequest().body("세션 정보가 유효하지 않습니다.");
        }

        Optional<UserProfile> userProfileOptional = userProfileJpaRepository.findById(userSession.getUserProfileId());

        if (userProfileOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 사용자 정보를 찾을 수 없습니다.");
        }

        Optional<Post> postOptional = postJpaRepository.findById(postId);

        if (postOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 게시물을 찾을 수 없습니다.");
        }

        Post post = postOptional.get();

        Optional<Comment> commentOptional = commentJpaRepository.findById(commentId);

        if (commentOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 댓글을 찾을 수 없습니다.");
        }
        UserProfile userProfile = userProfileOptional.get();
        Comment comment = commentOptional.get();

        if(!comment.getUserProfile().getUserProfileId().equals(userProfile.getUserProfileId())) {
            return ResponseEntity.badRequest().body("작성하신 사용자와 일치하지 않는 사용자입니다.");
        }

        commentJpaRepository.delete(comment);

        post.getPostView().commentCountDecrease();

        return ResponseEntity.ok("댓글 삭제 성공");
    }
}
