## 🌟 시끌벅적 놀이터(Post Service) 🌟

### 📜 프로젝트 개요

이 프로젝트는 게시물(Post), 댓글(Comment), 사용자(User) 관리를 포함하는 커뮤니티 백엔드 서비스입니다. **Spring Boot**를 기반으로 구축되었으며, **JWT(JSON Web Token)**를 이용한 인증/인가 시스템과 **AWS S3**를 이용한 파일 업로드를 지원합니다. 특히, 데이터베이스 부하를 줄이고 성능을 최적화하기 위한 다양한 스케줄러(Scheduler) 로직이 적용되어 있습니다.

### 🛠️ 주요 기술 스택

| 분류 | 기술 | 설명 |
| --- | --- | --- |
| **Backend** | Java 17, Spring Boot 3.x | 핵심 프레임워크 및 언어 |
| **Data** | Spring Data JPA, MySQL | 데이터베이스 ORM 및 영속성 관리 |
| **Auth** | JWT (Cookie-based) | 사용자 인증 및 권한 부여 |
| **Deployment** | AWS S3 | 이미지 파일 저장 및 관리 |
| **Optimization** | ConcurrnentHashMap, Scheduling | 조회수 캐싱 및 비동기 처리 |
| **Utilities** | Lombok, Validation | 개발 편의성 및 데이터 유효성 검증 |

Sheets로 내보내기

---

## 💻 핵심 기능 및 API 엔드포인트

### 1. 사용자 및 인증 관리 (`UserController`, `JwtController`) 🔑

JWT 기반의 인증 흐름을 쿠키를 통해 관리하며, `accessToken`과 `refreshToken`을 이용한 토큰 재발급(Refresh Token Rotation) 로직이 적용되어 보안과 사용성을 높였습니다.

| 기능 | HTTP Method | URL | 설명 |
| --- | --- | --- | --- |
| **회원가입** | `POST` | `/api/users/sign-up` | 신규 사용자 등록 |
| **로그인** | `POST` | `/api/users/login` | JWT 발급 및 쿠키 설정 |
| **회원정보 조회** | `GET` | `/api/users` | 로그인 사용자 정보 조회 |
| **프로필 수정** | `PUT` | `/api/users/profile` | 닉네임, 프로필 이미지 수정 |
| **비밀번호 변경** | `PUT` | `/api/users/password` | 사용자 비밀번호 변경 |
| **회원탈퇴** | `DELETE` | `/api/users` | 사용자 Soft-Delete 및 토큰 무효화 |
| **로그아웃** | `PUT` | `/api/users/log-out` | RefreshToken 무효화 및 쿠키 삭제 |
| **토큰 검증/재발급** | `GET` | `/api/jwt/validate` | 유효성 검사 및 만료 시 토큰 재발급 로직 |
| **서비스 약관** | `GET` | `/api/terms/signup` | 약관 페이지 뷰 제공 (Server Side View) |

Sheets로 내보내기

---

### 2. 게시물 관리 (`PostController`) 📝

게시물의 생성, 조회, 수정, 삭제 및 좋아요 기능을 제공합니다.

| 기능 | HTTP Method | URL | 설명 |
| --- | --- | --- | --- |
| **목록 조회** | `GET` | `/api/posts/list` | 페이지네이션 적용된 게시물 목록 조회 |
| **상세 조회** | `GET` | `/api/posts/{postId}` | 특정 게시물 상세 정보 조회 및 **조회수 캐싱 적용** |
| **게시물 생성** | `POST` | `/api/posts/create` | 새 게시물 등록 |
| **작성자 확인** | `GET` | `/api/posts/{postId}/check-writer` | 게시물 작성자와 로그인 사용자 일치 여부 확인 |
| **게시물 수정** | `PUT` | `/api/posts/{postId}/update` | 특정 게시물 수정 |
| **게시물 삭제** | `DELETE` | `/api/posts/{postId}/delete` | 특정 게시물 삭제 |
| **좋아요 처리** | `POST` | `/api/posts/{postId}/like` | 게시물 좋아요/좋아요 취소 토글 |
| **이미지 업로드** | `POST` | `/api/posts/image` | 임시 이미지 파일 업로드 (로컬 파일 저장 방식) |

Sheets로 내보내기

---

### 3. 댓글 관리 (`CommentController`) 💬

특정 게시물(`{postId}`)에 종속된 댓글에 대한 CRUD 기능을 제공합니다.

| 기능 | HTTP Method | URL | 설명 |
| --- | --- | --- | --- |
| **댓글 목록 조회** | `GET` | `/api/{postId}/comments` | 특정 게시물의 댓글 목록 조회 (페이지네이션) |
| **댓글 등록** | `POST` | `/api/{postId}/comments` | 새 댓글 등록 |
| **댓글 수정** | `PUT` | `/api/{postId}/comments/{commentId}` | 특정 댓글 수정 |
| **댓글 삭제** | `DELETE` | `/api/{postId}/comments/{commentId}` | 특정 댓글 삭제 |

Sheets로 내보내기

---

### 4. 파일 저장소 (AWS S3) 관리 (`S3Controller`) ☁️

클라이언트가 직접 AWS S3에 파일을 업로드할 수 있도록 Presigned URL을 발급합니다.

| 기능 | HTTP Method | URL | 설명 |
| --- | --- | --- | --- |
| **Presigned URL 발급** | `GET` | `/api/s3/presigned` | 파일명에 대한 AWS S3 업로드용 Presigned URL 생성 및 반환 |

Sheets로 내보내기

---

## 🚀 성능 최적화 및 스케줄러

### 1. 게시물 조회수 비동기 업데이트 (`PostViewSchedulerService`) 📈

- **문제:** 모든 게시물 조회 요청마다 DB의 조회수(`lookCount`)를 업데이트하면 I/O 부하가 커집니다.
- **해결:**
    1. `@GetMapping("/{postId}")` 요청 시, DB에 바로 반영하지 않고 **`ConcurrentHashMap`**(`viewCache`)에 게시물 ID별로 조회수를 누적(Merge)합니다.
    2. `@Scheduled(fixedRate = 10_000)`를 이용해 **10초**마다 캐시된 조회수를 DB에 **배치(Batch)** 형식으로 일괄 반영합니다.
    3. 이 방식은 DB I/O를 획기적으로 줄여 서비스의 **TPS(Transaction Per Second)**를 향상시킵니다.

### 2. 고아 파일(Orphan File) 정리 (`FileCleanupSchedulerService`) 🗑️

- **문제:** 사용자가 이미지를 업로드했으나 DB에 최종적으로 등록되지 않은 파일(고아 파일)이 서버 저장 공간을 낭비합니다.
- **해결:**
    1. `@Scheduled(fixedRate = 300000)`를 이용해 **5분**마다 실행됩니다.
    2. 로컬 **`uploads/`** 디렉터리의 모든 파일명과 DB에 저장된 **모든 사용자 프로필 이미지 URL의 파일명**을 비교합니다.
    3. DB에 경로가 없는 파일을 **"고아 파일"**로 판단하고 물리적으로 삭제하여 저장 공간을 정리합니다.
    - **파일명 비교 안정성:** 한글/특수문자 문제 방지를 위해 **URL 디코딩** 및 **문자열 정규화(`Normalizer.Form.NFC`)**를 적용했습니다.

### 3. Soft-Deleted 사용자 영구 삭제 (`UserCleanupSchedulerService`) 💀

- **문제:** 회원 탈퇴 시 즉시 DB에서 삭제하지 않고 Soft-Delete(`is_deleted = true`) 처리만 하여 일정 기간 복구 가능성을 열어두어야 합니다.
- **해결:**
    1. `@Scheduled(fixedRate = 30000)`를 이용해 주기적으로 실행됩니다.
    2. DB에서 `is_deleted = true`인 사용자 계정을 찾아 실제 **물리적 삭제(`delete`)**를 수행합니다.
    - **참고:** 현재는 30초마다 실행되도록 임시 설정되어 있으며, 프로덕션 환경에서는 보통 **매일 자정**에 실행되도록 설정됩니다.
