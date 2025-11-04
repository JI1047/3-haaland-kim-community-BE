package com.example.postService.service.user.impl;

import com.example.postService.dto.login.request.LoginRequestDto;
import com.example.postService.dto.user.request.CreateUserRequestDto;
import com.example.postService.dto.user.request.UpdateUserPasswordRequestDto;
import com.example.postService.dto.user.request.UpdateUserProfileRequestDto;
import com.example.postService.dto.user.response.CreateUserResponseDto;
import com.example.postService.dto.user.response.GetUserResponseDto;
import com.example.postService.entity.user.User;
import com.example.postService.entity.user.UserProfile;
import com.example.postService.entity.user.UserTerms;
import com.example.postService.jwt.CookieUtil;
import com.example.postService.jwt.TokenService;
import com.example.postService.mapper.user.UserMapper;
import com.example.postService.repository.token.RefreshTokenRepository;
import com.example.postService.repository.user.UserJpaRepository;
import com.example.postService.repository.user.UserProfileJpaRepository;
import com.example.postService.repository.user.UserTermsJpaRepository;
import com.example.postService.service.user.UserService;
import com.example.postService.util.FileStorage;
import com.example.postService.util.PasswordEncoderUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor//private final 로 선언된 객체들의 생성자들을 자동으로 생성해줌
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserJpaRepository userJpaRepository;
    private final UserTermsJpaRepository userTermsJpaRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;
    private final FileStorage fileStorage;

    /**
     * 회원가입 로직
     * 1. 요청 dto를 통해 이메일 중복 체크를 진행
     * 2. 중복되는 이메일 입력시 이미 존재하는 이메일 예외메세지 반환
     * 3. Bcrypt 방식으로 패스워드 암호화
     * 4. dto 와 mapper을 통해서 UserProfile 생성
     * 5. 암호화한 password로 요청 dto 업데이트
     * 5. 요청dto, UserProfile을 mapper을 통해 User 객체 생성
     * 6. User JPA메서드를 통해 회원저장
     * 7. User 객체를 mapper로 회원가입 응답dto로 변환 후 return
     */
    @Override
    @Transactional
    public CreateUserResponseDto signUp(CreateUserRequestDto dto) {


        //이메일 중복 시 예외 처리
        if (userJpaRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        //닉네임 중복 시 예외 처리
        if(userJpaRepository.existsByUserProfile_Nickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임 입니다.");
        }

        //BCrypt 방식으로 패스워드 암호화 진행
        String encodedPassword = PasswordEncoderUtil.encode(dto.getPassword());

        //userProfile 엔터티 생성
        UserProfile userProfile = userMapper.createUserRequestDtoToUserProfile(dto);

        //패스워드 암호화된 패스워드로 업데이트(mapper에서 실제 데이터를 사용하지 않게 하기 위해서)
        CreateUserRequestDto createUserRequestDto = CreateUserRequestDto.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .build();

        //user 엔터티 생성
        User user = userMapper.createUserRequestDto(createUserRequestDto, userProfile);


        //DB 저장
        userJpaRepository.save(user);

        //회원 약관 정보 생성 및 저장
        UserTerms userTerms = userMapper.TermsAgreementDtoToUserTerms(dto.getTermsAgreement(), user);

        userTermsJpaRepository.save(userTerms);

        return userMapper.userToCreateUserResponseDto(user);

    }


    /**로그인 처리 Service 로직
     * 1. 이메일로 사용자 조회 후 존재 여부 검증
     * 2. 암호화된 비밀번호와 dto.password를 통해 비밀번호 일치 여부 검증
     * 3. 인증 성공 시, 세션에 UserProfile 정보 저장 후 생성(UserProfile엔터티가 게시물 프로젝트에서 많이 사용되기때문)
     * ex. 댓글 작성,게시물 작성, 좋아요 처리 등등
     * 세션 처리는 Spring Security를 배우지 않았기 때문에
     * LoginCheckInterceptor을 통해 세션 확인 과정을 거치고
     * WebConfig을 통해 세션이 필요한 엔드포인트를 설정했습니다.
     *
     */
    @Override
    @Transactional
    public ResponseEntity<Map<String,Object>> login(LoginRequestDto dto, HttpServletResponse response) {

        //입력받은 email을 통해 사용자 존재 여부를 DB조회를 통해 확인
        User user= userJpaRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 사용자 입니다."));


        //passwordEncoderUtil에서 생성해놓은 matches메서드를 통해 입력 password와 암호화된 password를 비교
        if (!PasswordEncoderUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치 하지 않습니다.");
        }


        refreshTokenRepository.deleteByUser_UserId(user.getUserId());

        var tokenResponse = tokenService.generateAndSaveTokens(user);

        cookieUtil.addTokenCookies(response,tokenResponse);

        Map<String,Object> success = new HashMap<>();
        success.put("success", true);
        success.put("message", "로그인 성공");
        success.put("status", 200);
        success.put("accessToken", tokenResponse.getAccessToken());

        return ResponseEntity.ok(success);
    }

    /**회원 정보 조회 Service 로직
     * 1. 요청경로에 포함된 userId를 통해서 User조회
     * 2. 응답에 필요한 dto mapper을 통해 변환후 반환
     */
    @Override
    public ResponseEntity<GetUserResponseDto> get(HttpServletRequest httpServletRequest) {


        Long userId = (Long) httpServletRequest.getAttribute("userId");

        User user = userJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        System.out.println(user.getEmail());
        //Mapper을 통해 응답 dto 변환 후 반환
        return ResponseEntity.ok(userMapper.userToUGetUserResponseDto(user));
    }

    /**닉네임,프로필 이미지 수정 Service 로직
     * 1. 요청경로에 포함된 userId를 통해서 해당 UserProfile 객체를 DB에서 조회
     * 2. 해당 userProfile 객체가 없을 시 예외 처리
     * 3. UserProfile 클래스 업데이트 메서드 선언해놓은 것을 통해서 업데이트 진행
     * 4. 업데이트 진행 후 성공 메세지 반환

     */
    @Override
    @Transactional
    public ResponseEntity<String> updateProfile(UpdateUserProfileRequestDto dto, HttpServletRequest httpServletRequest) {


        Long userId = (Long) httpServletRequest.getAttribute("userId");
        if (userId == null) {
            throw new IllegalArgumentException("인증 정보가 없습니다. 로그인 해주세요.");
        }


        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        UserProfile userProfile = user.getUserProfile();



//      UserProfile클래스 업데이트 메서드를 통해서 profile 업데이트
        userProfile.updateProfile(dto.getNickname(), dto.getProfileImage());

        return ResponseEntity.ok("닉네임,프로필 이미지 수정 성공!");
    }

    /**
     *User Password 수정 로직
     * 1. 요청에 포함된 dto에서 비밀번호와 비밀 번호 확인 일치 여부 확인
     * 1-1. 일치 하지 않을 시 에러 메시지 반환
     * 2. 요청에 포함된 userId를 통해 DB에서 User조회
     * 2-1 해당 User객체 없을 시 예외 처리
     * 3. 입력받은 password BCrypt방식으로 암호화 진행
     * 4. User 클래스 업데이트 메서드를 통해서 비밀번호 업데이트
     * 5. 성공 메세지 리턴
     */
    @Override
    @Transactional
    public ResponseEntity<String> updatePassword(UpdateUserPasswordRequestDto dto, HttpServletRequest httpServletRequest) {


        Long userId = (Long) httpServletRequest.getAttribute("userId");

        if (userId == null) {
            throw new IllegalArgumentException("인증 정보가 없습니다. 로그인 해주세요.");
        }


        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.!");
        }//입력된 비밀번호/비밀번호 확인이 일치한지 확인 예외 처리

        //BCrypt 방식으로 패스워드 암호화 진행
        String encodedPassword = PasswordEncoderUtil.encode(dto.getNewPassword());

        //User클래스 업데이트 메서드를 통해 password 업데이트
        user.updatePassword(encodedPassword);

        return ResponseEntity.ok("비밀번호 변경 성공");


    }

    /**
     * 회원 삭제(soft_delete)
     * 1. 요청으로 들어온 userId를 통해 User객체 DB에서 조회
     * 1-1 해당하는 User 객체 없다면 에러 메세지 반환
     * 2. User클래스 updateDeleted메서드를 통해서 is_deleted true(삭제)로 업데이트 delete_at 시간 업데이트
     * 3. 사용자에겐 회원 탈퇴 성곰 메세지 반환
     * *
     */
    @Override
    @Transactional
    public ResponseEntity<String> softDelete(HttpServletRequest httpServletRequest) {


        Long userId = (Long) httpServletRequest.getAttribute("userId");

        if (userId == null) {
            throw new IllegalArgumentException("인증 정보가 없습니다. 로그인 해주세요.");
        }

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        //isDeleted true(삭제됨)으로 업데이트, deleted_at 업데이트
        user.updateDeleted();

        //사용자에게는 회원탈퇴 성공 메세지 반환
        return ResponseEntity.ok("회원탈퇴 성공");

    }



    /**회원정보 삭제 HardDelete버전
     * 위에 softDelete버전 메서드 service로직을 만들었습니다
     * DeleteMapping이지만 회원을 삭제 하는 것이 아닌
     * id_deleted,delete_At을 업데이트하여 논리적삭제를 하는 로직이기 때문에
     * hardDelete메서드 로직을 만들어 해당 사용자를 DB에서 제거하여 1번과제를 진행했습니다
     * 처음 계획은 관리자 역할을 만들어 관리자만 접근가능하게 하려했지만
     * 시간이 부족해서 이후에 관리자에서만 hardDelete를 접근할 있게 리팩토링 예정입니다.
     *
     * 1. 요청경로에 포함된 userId를 통해 User객체 조회
     * 2. User객체 없다면 예외처리
     * 3. 해당 user 삭제 진행
     * 3-1 User와 UserProfile이 cascade = CascadeType.ALL로 설정되어잇기 때문에
     * User삭제시 UserProfile도 동시에 삭제된다.
     * 4, 성공시 성공 메세지 반환
     */
//    @Transactional
//    @Override
//    public ResponseEntity<String> hardDelete(Long userId) {
//        Optional<User> userOptional = userJpaRepository.findById(userId);
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        User user = userOptional.get();
//
//        userJpaRepository.delete(user);
//
//        return ResponseEntity.ok("삭제가 완료됐습니다.");
//    }
}
