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
import com.example.postService.entity.user.UserProfile;
import com.example.postService.mapper.post.PostMapper;
import com.example.postService.repository.post.PostJpaRepository;
import com.example.postService.repository.post.PostLikeJpaRepository;
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
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final PostLikeJpaRepository postLikeJpaRepository;
    private final SessionManager sessionManager;


    /**
     * 게시물 생성 로직
     * 1. 로그인 한 사용자만 게시물 생성가능 하기 때문에
     * 2. 세션 검증 진행
     * 2-1. 세션이 없을 경우 로그인이 필요 에러 메세지 반환
     * 2-2  세션에 해당하는 User객체가 없을 경우 에러 메세지 반환
     * 3. UserSession객체의 UserProfileId를 통해 UserProfile 조회
     * 4. PostView 객체 생성
     * 4-1. 처음 생성 시에는 모두 0으로 설정되도록 클래스에서 설계
     * 5. mapper을 통해 postContent 객체 생성
     * 6. mapper을 통해 게시물 객체 생성
     * 7. 성공 메세지 반환
     *
     */
    @Override
    @Transactional
    public ResponseEntity<String> createPost(CreatePostRequestDto createPostRequestDto, HttpServletRequest httpServletRequest) {



        UserSession userSession = sessionManager.getSession(httpServletRequest);

        if (userSession == null || userSession.getUserProfileId() == null) {
            throw new IllegalArgumentException("접근 권한이 없습니다. 로그인 해주세요!");
        }

        UserProfile userProfile = userProfileJpaRepository.findById(userSession.getUserProfileId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 프로필을 찾을 수 없습니다."));


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
     * 포스트 수정 로직
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
     * 포스트 수정 로직
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
     * 게시물 리스트를 page(시작점, 불러올 게시물 size)를 설정해놓고
     *  jpa를 통해 findAll메서드를 호출해서 List 형태로 조회했습니다.
     *  이후 조회,댓글,좋아요 수를 불러오기 위해 lazy로 설정된 postView,UserProfile도를
     *  for문을 통해 불러온 list의 크기만큼 반복하여 조회했습니다.
     *  그러자 List posts를 조회할 때 한번 그리고 List의 크기만큼 postView,UserProfile도를 조회하는
     *  N+1문제를 마주쳣습니다.
     *  queryDSL을 직접 작성하여 fetch join을 통해서 관련된 poseView의 엔터티들을
     *  한번에 조회하여 쿼리문을 줄이는 설계를 통해 최적화를 진행했습니다.
     *
     *  1. 요청을 통해 포함된 page,size를 통해서 pageRequest 설정
     *  2. pageRequest를 포함해 만든 queryDSL로 해당하는 List의 크기만큼 post조회
     *  2-1. 관련 매핑 객체 postView,UserProfile도 한번에 조회
     *  3. 게시물 목록 조회 응답 dto List 생성
     *  3. for문을 통해 미리 불러온 posts에서 mapper를 통해 dto로 변경
     *  4. 응답 dto List 반환
     *
     */
    @Override//게시물 목록 조회
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
     *게시물 상세 조회 로직(댓글 포함)
     * 1. 요청된 postId를 통해 Optional<Post> 조회 (null판단을 위해)
     * 2. null일시 예외 처리
     * 3. post 객체를 통해 postContent(내용.이미지) 조회 후 객체 생성
     * 4. post 객체를 통해 PostView(조회,좋아요,댓글 수)조회 . 객체 생성
     * 5. post 객체를 통해 UserProfile(유저 프로필)조회 후 객체 생성
     * 6. QueryDSL를 통해 Post객체에 해당하는 댓글 리스트 조회
     * 7. GetCommentResponseDto(게시물 댓글 응답 dto user nickname,profileImage,댓글 내용 포함) dto리스트 선언
     * 8. 댓글 갯수 만큼 for문을 통해 리스트에 추가
     * 9. 최종 게시물 상세 조회 응답 dto 생성
     * 10. 게시물 상세 조회 응답 dto 반환
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
     * 1. 요청에 포함된 session 검증
     * 1-1 session이 없을 시 로그인 필요 에러 메세지 반환
     * 1-2 세션에 해당하는 user없을 시 해당 사용자 정보 없음 에러 메세지 반환
     * 2. 요청에 포함된 postId를 통해서 Post객체 조회
     * 2-1 해당하는 Post없다면 에러 메세지 반환
     * 3.PostLike 테이블에 <UserProfile,Post>를 모두 포함하는 데이터가 있는지 확인
     * 3-1. 해당하는 데이터가 있을 시 좋아요 취소 로직 실행(jpa를 통해 데이터 삭제 후 Post좋아요 수 감소) 후 좋아요 제거 메시지 반환
     * 3-2. 해당하는 데이터가 없을 시 좋아요 등록 로직 실행(jpa를 통해 데이터 등록 후 Post 좋아요 수 증가) 후 좋아요 등록 메세지 반환
     *
     *
     */
    @Transactional
    @Override
    public ResponseEntity<String> updatePostLike(Long postId, HttpServletRequest httpServletRequest) {


        UserSession userSession = sessionManager.getSession(httpServletRequest);

        if (userSession == null || userSession.getUserProfileId() == null) {
            throw new IllegalArgumentException("접근할 수 없습니다. 로그인 해주세요!");
        }

        UserProfile userProfile = userProfileJpaRepository.findById(userSession.getUserProfileId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 프로필을 찾을 수 없습니다."));

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

    @Override
    public ResponseEntity<Map<String, Boolean>> checkWriter(Long postId, HttpServletRequest httpServletRequest) {
        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));
        UserSession userSession = sessionManager.getSession(httpServletRequest);


        boolean isOwner = userSession.getUserProfileId()
                .equals(post.getUserProfile().getUserProfileId());
        if(!isOwner) {
            throw new IllegalArgumentException("로그인 한 사용자와 일치하지 않습니다.");
        }

        return ResponseEntity.ok(Map.of("match", true));
    }
}
