🌟 Post Service (Spring Boot)

"단순한 CRUD가 아니라, 현실 서비스의 문제를 해결하는 백엔드 아키텍처를 만들고 싶었습니다."

Spring Boot 기반으로 구현된 커뮤니티 서비스 백엔드로,
게시물(Post), 댓글(Comment), 사용자(User) 관리뿐 아니라
JWT 기반 인증, 조회수 비동기 처리, S3 Presigned URL 업로드, 고아 파일 정리 처리 등
실서비스에서 실제로 고려해야 하는 문제들을 설계 단계부터 반영하여 개발했습니다.

이 프로젝트는 단순 기술 사용을 넘어,

"왜 이런 구조로 설계했는가?"
"어떤 문제를 해결하기 위한 선택인가?"

를 중심으로 기술을 선택했습니다.

📌 목차

프로젝트 컨셉

기술 스택

핵심 기능 요약

API 상세

문제 해결 기록 (Core Engineering)

스케줄러 기반 자동화 처리

아키텍처 구성

이 프로젝트의 차별점

더 개선할 수 있는 방향

🎯 프로젝트 컨셉

단순 CRUD를 넘어 서비스 운영 관점에서 필요한 문제 해결 능력을 키우기 위한 프로젝트입니다.

특히 아래 세 가지 목표를 가지고 설계했습니다.

1️⃣ 리소스 효율성을 고려한 설계

조회수 증가를 요청 단위로 DB에 반영하면 부하 증가 → 비동기 캐싱 방식으로 해결

이미지 업로드 시 사용되지 않은 파일 누적 방지

탈퇴 유저 데이터 정리 자동화

2️⃣ 실제 서비스처럼 동작하는 인증 흐름 구현

AccessToken 만료 → RefreshToken으로 자동 재발급(RTR)

HttpOnly 쿠키 기반 보안 모델 적용

FE 전역 공통 로직과 통신하는 인증 API 제공

3️⃣ 프론트엔드와의 협업을 고려한 API 설계

FE가 직접 S3에 업로드할 수 있도록 Presigned URL 제공

게시물, 댓글, 유저 정보는 FE 요청 흐름 기준으로 설계

🛠️ 기술 스택
분류	기술	선택 이유
Backend	Java 17, Spring Boot 3.x	안정성 + 생산성
DB	MySQL + Spring Data JPA	직관적인 ORM 모델
Auth	JWT + HttpOnly Cookie	보안성과 편의성 공존
Infra	AWS S3	이미지 저장에 최적화
Optimization	ConcurrentHashMap + Scheduler	조회수 성능 최적화
Utility	Lombok, Validation	개발 효율성
💡 핵심 기능 요약
🔑 사용자 & 인증 (User, Jwt)

회원가입 / 로그인 / 로그아웃

JWT 기반 토큰 발급 및 쿠키 저장

만료된 AccessToken 자동 재발급(Refresh Token Rotation)

Soft Delete 계정 관리

프로필 이미지 & 개인정보 수정

📝 게시물 (Post)

게시물 작성 / 조회 / 수정 / 삭제

좋아요 기능

조회수 캐싱 후 비동기 반영(성능 최적화)

작성자 본인 확인 API 제공

💬 댓글 (Comment)

게시물별 CRUD

이벤트 위임 방식 FE 연동 가능 구조

☁️ 파일 업로드 (S3)

Presigned URL 발급

안전한 파일명 생성

FE → S3 직접 업로드 구조

📡 API 상세
1. 사용자 / 인증 API
기능	Method	URL	설명
회원가입	POST	/api/users/sign-up	신규 사용자 생성
로그인	POST	/api/users/login	JWT 발급 및 쿠키 저장
사용자 정보 조회	GET	/api/users	로그인 사용자 정보
프로필 수정	PUT	/api/users/profile	닉네임·이미지 수정
비밀번호 변경	PUT	/api/users/password	PW 변경
회원탈퇴	DELETE	/api/users	Soft delete
로그아웃	PUT	/api/users/log-out	쿠키 삭제 + RT 삭제
JWT 유효성 검사	GET	/api/jwt/validate	만료 시 자동 재발급
2. 게시물(Post) API
기능	Method	URL
목록 조회	GET	/api/posts/list
상세 조회	GET	/api/posts/{postId}
게시물 생성	POST	/api/posts/create
작성자 여부 확인	GET	/api/posts/{postId}/check-writer
게시물 수정	PUT	/api/posts/{postId}/update
게시물 삭제	DELETE	/api/posts/{postId}/delete
좋아요 토글	POST	/api/posts/{postId}/like
이미지 업로드	POST	/api/posts/image
3. 댓글(Comment) API
기능	Method	URL
댓글 목록 조회	GET	/api/{postId}/comments
댓글 작성	POST	/api/{postId}/comments
댓글 수정	PUT	/api/{postId}/comments/{commentId}
댓글 삭제	DELETE	/api/{postId}/comments/{commentId}
4. AWS S3 Presigned API
기능	Method	URL
Presigned URL 발급	GET	/api/s3/presigned
⚙️ 문제 해결 기록 (Core Engineering)
1️⃣ 조회수 증가 시 DB 과부하 문제
● 문제

게시물 조회 시마다 즉시 DB로 write → 트래픽 증가 시 I/O 폭증

● 해결

조회수는 실시간성을 요구하지 않음 → 캐싱 후 배치 반영

ConcurrentHashMap을 활용하여
postId → 누적 조회수 형태로 저장

10초마다 스케줄러가 DB에 일괄 업데이트

→ 결과: TPS 향상, DB 부하 급감

2️⃣ 이미지 고아 파일 문제
● 문제

이미지를 업로드했지만 DB에는 등록되지 않은 파일이 계속 쌓임 → 저장소 낭비

● 해결

스케줄러가 5분마다 실행되어

uploads/ 폴더 내부 파일명 → DB의 profileImage 파일명 비교

DB에 없는 파일을 자동 삭제

URL 디코딩 + 문자열 정규화(NFC)로 한글/특수문자 대응

3️⃣ 탈퇴 유저 데이터 누적 문제
● 문제

Soft Delete로 is_deleted만 true 처리되고 실제 삭제가 안됨

● 해결

스케줄러가 주기적으로 삭제 처리

RT도 즉시 무효화하여 보안 강화

🔄 스케줄러 기반 자동화 처리
스케줄러	주기	역할
PostViewSchedulerService	10초	조회수 누적 후 일괄 DB 반영
FileCleanupSchedulerService	5분	고아 파일 자동 삭제
UserCleanupSchedulerService	30초	Soft Deleted 유저 물리 삭제
🏛 아키텍처 구성
controller
 ├─ UserController
 ├─ JwtController
 ├─ PostController
 ├─ CommentController
 └─ S3Controller

service
 ├─ UserService
 ├─ PostService
 ├─ CommentService
 └─ TokenService

repository
 ├─ UserJpaRepository
 ├─ PostJpaRepository
 └─ RefreshTokenRepository

scheduler
 ├─ PostViewSchedulerService
 ├─ FileCleanupSchedulerService
 └─ UserCleanupSchedulerService

infra / util
 ├─ FileStorage
 ├─ FileNameUtil
 ├─ CookieUtil

⭐ 이 프로젝트의 차별점 (Strong Points)

우테코 스타일로 가장 잘 보여줘야 하는 부분!

✔ 단순 CRUD가 아닌, "운영되는 서비스"를 목표로 설계함

조회수 캐싱, 고아 파일 정리, Soft Delete 등
운영 환경에서 실제로 필요한 기능들을 직접 구현했습니다.

✔ RTR(Refresh Token Rotation) 전략 구현

실 서비스에서 사용하는 JWT 재발급 전략을 직접 구현하여 보안성 강화.

✔ FE 협업을 고려한 Presigned URL 설계

FE가 S3에 직접 업로드 → 서버 부하 감소 + 확장성 확보

✔ 문제 → 해결 기반의 개발 방식

모든 기능이 “왜 필요한가?”에서 출발하여 설계됨
→ 상위 레벨의 엔지니어링 역량을 보여주는 지점

🚀 더 개선할 수 있는 방향

Redis 기반 조회수 캐싱 전환

Elasticsearch를 이용한 검색 기능

이미지 처리 서버(Lambda 활용)

Kafka 기반 비동기 이벤트 아키텍처 적용
