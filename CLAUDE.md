# CLAUDE.md

## Project Overview

커뮤니티 게시판 서비스. 사용자가 가입하고, 글을 쓰고, 댓글을 달며 소통하는 기본적인 게시판을 구현한다.

핵심 도메인:
- **Member** — 회원 (가입, 프로필 조회/수정, 비밀번호 변경, 탈퇴) ✅ 구현 완료
- **Board** — 게시판 (CRUD) ✅ 구현 완료
- **Post** — 게시글 (CRUD, 조회수, 페이징) ✅ 구현 완료
- **Comment** — 댓글 (예정)

## Tech Stack

- Java 25
- Spring Boot 4.0.2
- Gradle (Kotlin DSL) — wrapper 9.3.1
- Spring Web (REST API)
- Spring Validation (jakarta.validation)
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
│   ├── domain/           Post.java, PostRepository.java (Port)
│   ├── infra/            InMemoryPostRepository.java
│   ├── createpost/       CreatePost UseCase + Controller + DTOs
│   ├── getpost/          GetPost UseCase + Controller + DTOs
│   ├── listposts/        ListPosts UseCase + Controller + DTOs
│   ├── updatepost/       UpdatePost UseCase + Controller + DTOs
│   └── deletepost/       DeletePost UseCase + Controller
└── global/
    └── exception/        EntityNotFoundException, GlobalExceptionHandler

src/test/java/com/jongwon/monad/
├── fixture/              BoardFixture, PostFixture, MemberFixture
├── member/
│   ├── domain/           MemberTest (15 tests)
│   ├── fake/             FakeMemberRepository
│   └── {slice}/          각 UseCase 테스트
├── board/
│   ├── domain/           BoardTest
│   ├── fake/             FakeBoardRepository
│   └── {slice}/          각 UseCase 테스트
└── post/
    ├── domain/           PostTest
    ├── fake/             FakePostRepository
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

```
# Member
POST   /api/v1/members                          회원가입
GET    /api/v1/members/{id}                     프로필 조회
PUT    /api/v1/members/{id}                     프로필 수정
PATCH  /api/v1/members/{id}/password            비밀번호 변경
DELETE /api/v1/members/{id}                     회원 탈퇴

# Board
POST   /api/v1/boards                           게시판 생성
GET    /api/v1/boards                           게시판 목록
GET    /api/v1/boards/{id}                      게시판 조회
PUT    /api/v1/boards/{id}                      게시판 수정
DELETE /api/v1/boards/{id}                      게시판 삭제

# Post
POST   /api/v1/boards/{boardId}/posts           게시글 작성
GET    /api/v1/boards/{boardId}/posts           게시글 목록 (페이징)
GET    /api/v1/boards/{boardId}/posts/{postId}  게시글 조회 (조회수 증가)
PUT    /api/v1/boards/{boardId}/posts/{postId}  게시글 수정
DELETE /api/v1/boards/{boardId}/posts/{postId}  게시글 삭제
```

## Build & Run

```bash
./gradlew build
./gradlew bootRun    # http://localhost:8080
./gradlew test
```

## Code Conventions

- **도메인 엔티티**: 순수 Java (프레임워크 어노테이션 없음), 정적 팩토리 메서드, 도메인 검증 내장
- **패키지**: Vertical Slice 단위 (lowercase: `board.createboard`, `member.signup`)
- **Port**: 도메인 패키지에 Repository 인터페이스 정의, infra 패키지에 구현체
- **DTO**: record 사용, jakarta.validation 어노테이션 (@NotBlank 등)
- **UseCase**: @Service + constructor injection
- **Controller**: @RestController + constructor injection, ResponseEntity 반환
- **테스트**: Fake 객체 + TestFixture 사용, DB 의존 없는 단위 테스트
- **도메인 간 참조**: ID 참조만 사용 (객체 참조 없음, 예: Post.boardId)

## Important Notes

- 현재 브랜치: `feature/standard-board-crud`
- DB 미정 — InMemory Adapter로 동작 중, DB 선택 후 Adapter만 교체하면 됨
- Post.author는 현재 String — 추후 memberId(Long)로 전환 예정
- 인증/인가 (Spring Security, JWT) 미구현
- 상세 개발 계획: `docs/crud.md`, `docs/member.md`
