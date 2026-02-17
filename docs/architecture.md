# Architecture

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
│   ├── infra/            FakeMemberRepository(@Profile local), JdbcMemberRepository(@Profile prod)
│   ├── signup/           SignUp UseCase + Controller + DTOs
│   ├── getmember/        GetMember UseCase + Controller + DTOs
│   ├── updatemember/     UpdateMember UseCase + Controller + DTOs
│   ├── changepassword/   ChangePassword UseCase + Controller + DTOs
│   └── deletemember/     DeleteMember UseCase + Controller
├── board/
│   ├── domain/           Board.java, BoardRepository.java (Port)
│   ├── infra/            FakeBoardRepository(@Profile local), JdbcBoardRepository(@Profile prod)
│   ├── createboard/      CreateBoard UseCase + Controller + DTOs
│   ├── getboard/         GetBoard UseCase + Controller + DTOs
│   ├── listboards/       ListBoards UseCase + Controller + DTOs
│   ├── updateboard/      UpdateBoard UseCase + Controller + DTOs
│   └── deleteboard/      DeleteBoard UseCase + Controller
├── post/
│   ├── domain/           Post.java (memberId 기반), PostRepository.java (Port)
│   ├── infra/            FakePostRepository(@Profile local), JdbcPostRepository(@Profile prod)
│   ├── createpost/       CreatePost UseCase + Controller + DTOs
│   ├── getpost/          GetPost UseCase + Controller + DTOs (닉네임 조회)
│   ├── listposts/        ListPosts UseCase + Controller + DTOs (닉네임 조회)
│   ├── updatepost/       UpdatePost UseCase + Controller + DTOs (본인 확인)
│   └── deletepost/       DeletePost UseCase + Controller (본인 확인)
├── comment/
│   ├── domain/           Comment.java (memberId 기반, @멘션 파싱), CommentRepository.java (Port)
│   ├── infra/            FakeCommentRepository(@Profile local), JdbcCommentRepository(@Profile prod)
│   ├── createcomment/    CreateComment UseCase + Controller + DTOs
│   ├── createreply/      CreateReply UseCase + Controller + DTOs (depth 1 제한)
│   ├── listcomments/     ListComments UseCase + Controller + DTOs (계층 구조, 닉네임 조회)
│   ├── updatecomment/    UpdateComment UseCase + Controller + DTOs (본인 확인)
│   └── deletecomment/    DeleteComment UseCase + Controller (본인 확인, 대댓글 cascade)
└── global/
    ├── exception/        EntityNotFoundException, GlobalExceptionHandler
    └── security/         SecurityConfig, JwtAuthenticationFilter, JwtTokenProvider,
                          BcryptPasswordEncoder, AuthenticationPrincipal, AuthAccessDeniedHandler
```

## Vertical Slice 패턴

각 UseCase는 독립된 패키지로 구성:

```
{slice}/
├── {Slice}Request.java      (record, jakarta.validation)
├── {Slice}Response.java     (record)
├── {Slice}UseCase.java      (@Service, constructor injection)
└── {Slice}Controller.java   (@RestController, ResponseEntity)
```

## Test Structure

```
src/test/java/com/jongwon/monad/
├── fixture/              BoardFixture, PostFixture, MemberFixture, CommentFixture
├── auth/
│   ├── fake/             FakePasswordEncoder, FakeTokenProvider
│   ├── login/            LoginUseCaseTest
│   └── refresh/          RefreshUseCaseTest
├── member/
│   ├── domain/           MemberTest
│   └── {slice}/          각 UseCase 테스트
├── board/
│   ├── domain/           BoardTest
│   └── {slice}/          각 UseCase 테스트
├── post/
│   ├── domain/           PostTest
│   └── {slice}/          각 UseCase 테스트
└── comment/
    ├── domain/           CommentTest
    └── {slice}/          각 UseCase 테스트
```

- Repository Fake 객체는 `src/main/.../infra/`에 위치 (local 프로필 + 테스트 공용)
- Auth Fake(FakePasswordEncoder, FakeTokenProvider)는 테스트 전용 (`src/test/auth/fake/`)
- TestFixture로 도메인 객체 생성 (`src/test/fixture/`)

## Auth

- JWT 기반 인증: Access Token (30분) + Refresh Token (7일)
- 비밀번호: BCrypt 해싱 (PasswordEncoder Port로 추상화)
- Controller에서 `@AuthenticationPrincipal AuthenticationPrincipal principal`로 인증 정보 추출
- Post/Comment: memberId(Long) 기반, 조회 시 MemberRepository로 닉네임 조회
