package com.example.postService.service.post.impl;

import com.example.postService.dto.post.response.GetPostListResponseDto;
import com.example.postService.dto.post.response.GetPostListResponseWrapperDto;
import com.example.postService.dto.post.response.GetPostResponseDto;
import com.example.postService.dto.post.resquest.CreatePostRequestDto;
import com.example.postService.dto.post.resquest.UpdatePostRequestDto;
import com.example.postService.dto.user.session.UserSession;
import com.example.postService.entity.post.Post;
import com.example.postService.entity.post.PostContent;
import com.example.postService.entity.post.PostLike;
import com.example.postService.entity.post.PostView;
import com.example.postService.entity.user.User;
import com.example.postService.entity.user.UserProfile;
import com.example.postService.jwt.CookieUtil;
import com.example.postService.jwt.TokenService;
import com.example.postService.mapper.post.PostMapper;
import com.example.postService.repository.post.PostJpaRepository;
import com.example.postService.repository.post.PostLikeJpaRepository;
import com.example.postService.repository.token.RefreshTokenRepository;
import com.example.postService.repository.user.UserJpaRepository;
import com.example.postService.repository.user.UserProfileJpaRepository;
import com.example.postService.service.post.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostJpaRepository postJpaRepository;
    private final PostLikeJpaRepository postLikeJpaRepository;
    private final UserJpaRepository userJpaRepository;

    /**
     * 게시물 생성 로직
     * 1. jwt에 포함된 userId를 추출
     * 2. 추출한 userId를 통해 User 객체 조회
     * 2-1. 없을 시 로그인 필요 에러 메세지 반환
     * 3. 조회한 User 객체를 통해 UserProfile 객체 조회
     * 4. PostView 객체 생성
     * 5. mapper을 통해 postContent 객체 생성
     * 6. mapper을 통해 게시물 객체 생성
     * 7. 게시물 JpaRepository를 통해 DB 저장
     * 8. 성공 메세지 반환
     *
     */
    @Override
    @Transactional
    public ResponseEntity<String> createPost(CreatePostRequestDto createPostRequestDto, HttpServletRequest httpServletRequest) {

        Long userId = (Long) httpServletRequest.getAttribute("userId");

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다!"));

        UserProfile userProfile = user.getUserProfile();

        //PostView 객체 생성
        PostView postView = PostView.builder().build();

        //PostContent객체 생성
        PostContent postContent = postMapper.postContentDtoToPostContent(createPostRequestDto);

        //Mapper을 통해 게시물 객체 생성
        Post post = postMapper.toPost(createPostRequestDto, postView, userProfile, postContent);

        //게시물 저장
        postJpaRepository.save(post);

        return ResponseEntity.ok("게시글 생성 성공!");

    }

    /**
     * 게시물 수정 로직
     * 1. 요청에 포함됨 postId를 통해 Post 조회
     * 2. 해당하는 Post 객체 없을 시 에러 처리
     * 3. Post를 통해 PostContent 조회
     * 4. 내용,이미지 변경 시 PostContent에서 정의한 update메서드 호출
     * 5. 제목 변경 시 Post클래스에서 정의한 update 호출
     * 6. 성공 메세지 반환
     */
    @Transactional
    @Override
    public ResponseEntity<String> updatePost(UpdatePostRequestDto dto, Long postId, HttpServletRequest httpServletRequest) {

        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));

        PostContent postContent = post.getPostContent();

        postContent.updatePostContent(dto);

        post.updatePost(dto);

        return ResponseEntity.ok("게시물 수정 성공");
    }
    /**
     * 게시물 삭제 로직
     * 1. 요청에 포함됨 postId를 통해 Post 조회
     * 2. 해당하는 Post 객체 없을 시 에러 처리
     * 3. Post JPA 메서드를 통해서 해당 Post 삭제
     * 4. 성공 메세지 반환
     */
    @Transactional
    @Override
    public ResponseEntity<String> deletePost(Long postId) {
        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));

        postJpaRepository.delete(post);

        return ResponseEntity.ok("게시물 삭제 성공");
    }

    /**게시물 목록 조회 로직
     *
     *  1. 요청을 통해 포함된 page,size를 통해서 pageRequest 객체 생성
     *  2. pageRequest를 포함해 Page<Post> 객체 생성
     *  3. 게시물 목록 조회 응답 Dto List 생성
     *  4. for문을 통해 posts에서 postView와 UserProfile을 추출하여
     *  4-1. 게시물 목록 응답 dto 생성 후 List에 추가
     *  5. 무한 스크롤 구현을 위한 WrapperDto로 변환 후 반환
     */
    @Override
    public ResponseEntity<GetPostListResponseWrapperDto> getPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Post> posts = postJpaRepository.findListPostQueryDSL(pageRequest);

        List<GetPostListResponseDto> responseDtoList = new ArrayList<>();

        for (Post post : posts) {
            PostView postView = post.getPostView();

            UserProfile userProfile = post.getUserProfile();

            GetPostListResponseDto getListPostResponseDto =
                    postMapper.toGetPostListResponseDto(post, postView, userProfile);

            responseDtoList.add(getListPostResponseDto);

        }

        GetPostListResponseWrapperDto responseWrapperDto= new GetPostListResponseWrapperDto(responseDtoList,posts.hasNext());

        return ResponseEntity.ok(responseWrapperDto);

    }

    /**
     *게시물 상세 조회 로직
     * 1. 요청된 postId를 통해 Post 객체 조회
     * 2. post 객체를 통해 postContent(내용.이미지) 조회 후 객체 생성
     * 3. post 객체를 통해 PostView(조회,좋아요,댓글 수)조회 . 객체 생성
     * 4. post 객체를 통해 UserProfile(유저 프로필)조회 후 객체 생성
     * 5. GetCommentResponseDto(게시물, 게시물 내용, 게시물 count 관련, 작성자 프로필) 선언
     * 6. 게시물 상세 조회 응답 dto 반환
     */
    @Override
    public ResponseEntity<GetPostResponseDto> getPost(Long postId) {
        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));

        PostContent postContent = post.getPostContent();

        PostView postView = post.getPostView();

        UserProfile userProfile = post.getUserProfile();

        GetPostResponseDto getPostResponseDto = postMapper.toGetPostResponseDto(post, postContent, postView, userProfile);

        return ResponseEntity.ok(getPostResponseDto);

    }

    /**
     * 게시물 좋아요 로직
     * 요청을 통해서 UserProfile과 Post객체를 받고
     * PostLike 테이블에 이 둘을 모두 포함하는 객체가 존재한다면 좋아효 제거 로직 처리/아니라면 좋아요 추가 로직 처리를 진행
     * 1. jwt에 포함된 userId를 추출
     * 2. 추출한 userId를 통해 User 객체 조회
     * 2-1. 없을 시 로그인 필요 에러 메세지 반환
     * 3. 요청에 포함된 postId를 통해서 Post객체 조회
     * 3-1 해당하는 Post없다면 에러 메세지 반환
     * 4.PostLike 테이블에 <UserProfile,Post>를 모두 포함하는 데이터가 있는지 확인
     * 4-1. 해당하는 데이터가 있을 시 좋아요 취소 로직 실행(jpa를 통해 데이터 삭제 후 Post좋아요 수 감소) 후 좋아요 제거 메시지 반환
     * 4-2. 해당하는 데이터가 없을 시 좋아요 등록 로직 실행(jpa를 통해 데이터 등록 후 Post 좋아요 수 증가) 후 좋아요 등록 메세지 반환
     */
    @Transactional
    @Override
    public ResponseEntity<String> updatePostLike(Long postId, HttpServletRequest httpServletRequest) {


        Long userId = (Long) httpServletRequest.getAttribute("userId");

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        UserProfile userProfile = user.getUserProfile();

        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));

        boolean alreadyLiked = postLikeJpaRepository.existsByPostAndUserProfile(post, userProfile);

        if (alreadyLiked) {
            // 좋아요 취소
            postLikeJpaRepository.deleteByPostAndUserProfile(post, userProfile);
            post.getPostView().likeCountDecrease();
            return ResponseEntity.ok("좋아요 제거");
        } else {
            // 좋아요 추가
            PostLike postLikeEntity = postMapper.toPostLike(post, userProfile);
            postLikeJpaRepository.save(postLikeEntity);
            post.getPostView().likeCountIncrease();
            return ResponseEntity.ok("좋아요 생성");
        }
    }

    /**
     * 게시글 작성자와 로그인 한 사용자가 일치하는지 확인하는 Service 메서드
     * 1. Request에 포함된 PostId를 통해 Post 객체 조회
     * 2. jwt에 포함된 userId를 추출
     * 3. 추출한 userId를 통해 User 객체 조회
     * 3-1. 없을 시 로그인 필요 에러 메세지 반환
     * 4. 작성자의 UserProfileId와 게시물의 작성자 UserProfileId가 동일한지 확인
     * 4-1. 동일하지 않다면 로그인 한 사용자와 일치하지 않다는 에러 메세지 반환
     * 4-2. 동일하다면 match 문자열 반환
     */
    @Override
    public ResponseEntity<Map<String, Boolean>> checkWriter(Long postId, HttpServletRequest httpServletRequest) {
        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));

        Long userId = (Long) httpServletRequest.getAttribute("userId");

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        UserProfile userProfile = user.getUserProfile();


        boolean isOwner = userProfile.getUserProfileId()
                .equals(post.getUserProfile().getUserProfileId());

        if(!isOwner) {
            throw new IllegalArgumentException("로그인 한 사용자와 일치하지 않습니다.");
        }

        return ResponseEntity.ok(Map.of("match", true));
    }
}
