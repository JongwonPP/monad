# CLAUDE.md

## Project Overview

커뮤니티 게시판 서비스. 사용자가 가입하고, 글을 쓰고, 댓글을 달며 소통하는 기본적인 게시판을 구현한다.

핵심 도메인:
- **Member** — 회원 (가입, 프로필 조회/수정, 비밀번호 변경, 탈퇴) ✅ 구현 완료
- **Board** — 게시판 (CRUD) ✅ 구현 완료
- **Post** — 게시글 (CRUD, 조회수, 페이징, 본인 확인) ✅ 구현 완료
- **Comment** — 댓글/대댓글 (CRUD, @멘션, 본인 확인) ✅ 구현 완료
- **Auth** — 인증/인가 (JWT 로그인, 토큰 갱신, Spring Security) ✅ 구현 완료

## Tech Stack

- Java 25
- Spring Boot 4.0.2
- Gradle (Kotlin DSL) — wrapper 9.3.1
- Spring Web (REST API)
- Spring Validation (jakarta.validation)
- Spring Security (JWT 인증, 필터 체인)
- JJWT (io.jsonwebtoken:jjwt-api 0.12.6)
- Lombok
- DB — 미정 (현재 InMemory Adapter 사용, 추후 교체)

## Architecture

- **Vertical Slice Architecture** — UseCase 단위로 코드 조직 (기술 계층 분리 X)
- **DDD** — 순수 Java 도메인 엔티티, 프레임워크 무의존
- **Port/Adapter** — Repository 인터페이스(Port)로 DB 추상화, InMemory Adapter로 동작
- **TDD** — 도메인 규칙 테스트 + UseCase 테스트 (Fake 객체 + TestFixture)

## Project Structure

```
src/main/java/com/jongwon/monad/
├── MonadApplication.java
├── auth/
│   ├── domain/           PasswordEncoder.java, TokenProvider.java (Port)
│   ├── login/            Login UseCase + Controller + DTOs
│   └── refresh/          Refresh UseCase + Controller + DTOs
├── member/
│   ├── domain/           Member.java, MemberRepository.java (Port)
│   ├── infra/            InMemoryMemberRepository.java
│   ├── signup/           SignUp UseCase + Controller + DTOs
│   ├── getmember/        GetMember UseCase + Controller + DTOs
│   ├── updatemember/     UpdateMember UseCase + Controller + DTOs
│   ├── changepassword/   ChangePassword UseCase + Controller + DTOs
│   └── deletemember/     DeleteMember UseCase + Controller
├── board/
│   ├── domain/           Board.java, BoardRepository.java (Port)
│   ├── infra/            InMemoryBoardRepository.java
│   ├── createboard/      CreateBoard UseCase + Controller + DTOs
│   ├── getboard/         GetBoard UseCase + Controller + DTOs
│   ├── listboards/       ListBoards UseCase + Controller + DTOs
│   ├── updateboard/      UpdateBoard UseCase + Controller + DTOs
│   └── deleteboard/      DeleteBoard UseCase + Controller
├── post/
│   ├── domain/           Post.java (memberId 기반), PostRepository.java (Port)
│   ├── infra/            InMemoryPostRepository.java
│   ├── createpost/       CreatePost UseCase + Controller + DTOs
│   ├── getpost/          GetPost UseCase + Controller + DTOs (닉네임 조회)
│   ├── listposts/        ListPosts UseCase + Controller + DTOs (닉네임 조회)
│   ├── updatepost/       UpdatePost UseCase + Controller + DTOs (본인 확인)
│   └── deletepost/       DeletePost UseCase + Controller (본인 확인)
├── comment/
│   ├── domain/           Comment.java (memberId 기반, @멘션 파싱), CommentRepository.java (Port)
│   ├── infra/            InMemoryCommentRepository.java
│   ├── createcomment/    CreateComment UseCase + Controller + DTOs
│   ├── createreply/      CreateReply UseCase + Controller + DTOs (depth 1 제한)
│   ├── listcomments/     ListComments UseCase + Controller + DTOs (계층 구조, 닉네임 조회)
│   ├── updatecomment/    UpdateComment UseCase + Controller + DTOs (본인 확인)
│   └── deletecomment/    DeleteComment UseCase + Controller (본인 확인, 대댓글 cascade)
└── global/
    ├── exception/        EntityNotFoundException, GlobalExceptionHandler
    └── security/         SecurityConfig, JwtAuthenticationFilter, JwtTokenProvider,
                          BcryptPasswordEncoder, AuthenticationPrincipal, AuthAccessDeniedHandler

src/test/java/com/jongwon/monad/
├── fixture/              BoardFixture, PostFixture, MemberFixture, CommentFixture
├── auth/
│   ├── fake/             FakePasswordEncoder, FakeTokenProvider
│   ├── login/            LoginUseCaseTest
│   └── refresh/          RefreshUseCaseTest
├── member/
│   ├── domain/           MemberTest
│   ├── fake/             FakeMemberRepository
│   └── {slice}/          각 UseCase 테스트
├── board/
│   ├── domain/           BoardTest
│   ├── fake/             FakeBoardRepository
│   └── {slice}/          각 UseCase 테스트
├── post/
│   ├── domain/           PostTest
│   ├── fake/             FakePostRepository
│   └── {slice}/          각 UseCase 테스트
└── comment/
    ├── domain/           CommentTest
    ├── fake/             FakeCommentRepository
    └── {slice}/          각 UseCase 테스트
```

각 Vertical Slice 구조:
```
{slice}/
├── {Slice}Request.java      (record, jakarta.validation)
├── {Slice}Response.java     (record)
├── {Slice}UseCase.java      (@Service, constructor injection)
└── {Slice}Controller.java   (@RestController, ResponseEntity)
```

## API Endpoints

### 공개 API (인증 불필요)
```
POST   /api/v1/members                                  회원가입
POST   /api/v1/auth/login                               로그인 (JWT 발급)
POST   /api/v1/auth/refresh                             토큰 갱신
GET    /api/v1/boards                                   게시판 목록
GET    /api/v1/boards/{id}                              게시판 조회
GET    /api/v1/boards/{boardId}/posts                   게시글 목록 (페이징)
GET    /api/v1/boards/{boardId}/posts/{postId}          게시글 조회 (조회수 증가)
GET    /api/v1/posts/{postId}/comments                  댓글 목록 (계층 구조)
```

### 인증 필요 API (Bearer Token)
```
# Board
POST   /api/v1/boards                                   게시판 생성
PUT    /api/v1/boards/{id}                              게시판 수정
DELETE /api/v1/boards/{id}                              게시판 삭제

# Post (작성: 인증된 사용자, 수정/삭제: 본인만)
POST   /api/v1/boards/{boardId}/posts                   게시글 작성
PUT    /api/v1/boards/{boardId}/posts/{postId}          게시글 수정 (본인)
DELETE /api/v1/boards/{boardId}/posts/{postId}          게시글 삭제 (본인)

# Comment (작성: 인증된 사용자, 수정/삭제: 본인만)
POST   /api/v1/posts/{postId}/comments                  댓글 작성
POST   /api/v1/posts/{postId}/comments/{id}/replies     대댓글 작성
PUT    /api/v1/posts/{postId}/comments/{id}             댓글 수정 (본인)
DELETE /api/v1/posts/{postId}/comments/{id}             댓글 삭제 (본인)

# Member (본인만)
GET    /api/v1/members/{id}                             프로필 조회
PUT    /api/v1/members/{id}                             프로필 수정
PATCH  /api/v1/members/{id}/password                    비밀번호 변경
DELETE /api/v1/members/{id}                             회원 탈퇴
```

## Auth

- JWT 기반 인증: Access Token (30분) + Refresh Token (7일)
- 비밀번호: BCrypt 해싱 (PasswordEncoder Port로 추상화)
- Controller에서 `@AuthenticationPrincipal AuthenticationPrincipal principal`로 인증 정보 추출
- Post/Comment: memberId(Long) 기반, 조회 시 MemberRepository로 닉네임 조회

## Build & Run

```bash
./gradlew build
./gradlew bootRun    # http://localhost:8080
./gradlew test
```

## Code Conventions

- **도메인 엔티티**: 순수 Java (프레임워크 어노테이션 없음), 정적 팩토리 메서드, 도메인 검증 내장
- **패키지**: Vertical Slice 단위 (lowercase: `board.createboard`, `member.signup`)
- **Port**: 도메인 패키지에 인터페이스 정의 (Repository, PasswordEncoder, TokenProvider), infra/security 패키지에 구현체
- **DTO**: record 사용, jakarta.validation 어노테이션 (@NotBlank 등)
- **UseCase**: @Service + constructor injection
- **Controller**: @RestController + constructor injection, ResponseEntity 반환
- **테스트**: Fake 객체 + TestFixture 사용, DB 의존 없는 단위 테스트
- **도메인 간 참조**: ID 참조만 사용 (객체 참조 없음, 예: Post.boardId, Post.memberId)

## Important Notes

- 현재 브랜치: `feature/standard-board-crud`
- DB 미정 — InMemory Adapter로 동작 중, DB 선택 후 Adapter만 교체하면 됨
- 상세 개발 계획: `docs/crud.md`, `docs/member.md`, `docs/comment.md`, `docs/auth.md`
