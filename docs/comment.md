# 댓글/대댓글(Comment) 도메인 개발 계획

## 1. 목표

게시글에 댓글과 대댓글을 작성할 수 있는 기능을 구현한다.
댓글은 1단계 대댓글까지 지원한다 (대대댓글 없음 — 커뮤니티 게시판 기본 구조).
댓글/대댓글 작성 시 `@닉네임` 으로 다른 회원을 멘션(태그)할 수 있다.

### 설계 원칙

기존 도메인과 동일:
- Vertical Slice Architecture
- DDD — 순수 Java 도메인 엔티티, Port 인터페이스
- TDD — 테스트 먼저 작성
- DB 비의존 — InMemory Adapter + Fake

---

## 2. 도메인 모델

### 2.1 Comment (Aggregate Root)

| 필드        | 타입              | 제약 조건    | 설명                              |
|-----------|-----------------|----------|---------------------------------|
| id        | Long            | PK       | 식별자                             |
| postId    | Long            | NOT NULL | 소속 게시글 ID                       |
| parentId  | Long            | nullable | 부모 댓글 ID (null이면 일반 댓글, 있으면 대댓글) |
| author    | String          | NOT NULL | 작성자 (추후 memberId로 전환)           |
| content   | String          | NOT NULL | 댓글 내용 (@닉네임 멘션 포함 가능)          |
| mentions  | List&lt;String&gt; |          | 멘션된 닉네임 목록 (content에서 파싱)       |
| createdAt | LocalDateTime   |          | 작성 시각                           |
| updatedAt | LocalDateTime   |          | 수정 시각                           |

**도메인 규칙:**
- content: 빈 문자열 불가, 최대 500자
- author: 빈 문자열 불가
- postId: null 불가
- parentId가 있으면 대댓글 — 대댓글에 대한 대댓글(depth 2+)은 허용하지 않음
  - 이 검증은 UseCase에서 수행 (parentId의 parentId가 null인지 확인)
- mentions: content에서 `@닉네임` 패턴을 파싱하여 자동 추출
  - 정규식: `@([가-힣a-zA-Z0-9_]{2,20})` (닉네임 규칙과 동일: 2~20자)
  - 중복 제거
  - 멘션 파싱은 도메인 로직 (Comment 내부에서 수행)
  - 멘션된 닉네임의 존재 여부 검증은 UseCase에서 MemberRepository를 통해 수행

**메서드:**
- `Comment.create(postId, parentId, author, content)` — 정적 팩토리, content에서 멘션 자동 파싱
- `update(content)` — 내용 수정, 멘션 재파싱, updatedAt 갱신
- `assignId(Long id)`
- `isReply()` — parentId != null이면 true
- `getMentions()` — 멘션된 닉네임 목록 (unmodifiable)
- `static parseMentions(String content)` — content에서 @닉네임 추출 (내부 유틸)

---

## 3. API 명세

### `/api/v1/posts/{postId}/comments`

| Method | Endpoint                                              | 설명       | Request Body          | Response       |
|--------|-------------------------------------------------------|----------|-----------------------|----------------|
| POST   | `/api/v1/posts/{postId}/comments`                     | 댓글 작성    | `{ author, content }` | 201 Created    |
| POST   | `/api/v1/posts/{postId}/comments/{commentId}/replies` | 대댓글 작성   | `{ author, content }` | 201 Created    |
| GET    | `/api/v1/posts/{postId}/comments`                     | 댓글 목록 조회 | —                     | 200 OK         |
| PUT    | `/api/v1/posts/{postId}/comments/{commentId}`         | 댓글 수정    | `{ content }`         | 200 OK         |
| DELETE | `/api/v1/posts/{postId}/comments/{commentId}`         | 댓글 삭제    | —                     | 204 No Content |

- content에 `@닉네임`을 포함하면 멘션으로 처리
- 존재하지 않는 닉네임을 멘션하면 무시 (에러 아님, 유효한 멘션만 저장)

**목록 조회 응답 구조** — 댓글 + 대댓글 + 멘션 정보 포함:
```json
{
  "comments": [
    {
      "id": 1,
      "author": "작성자",
      "content": "@답변자 이 부분 어떻게 생각해?",
      "mentions": ["답변자"],
      "createdAt": "...",
      "replies": [
        {
          "id": 3,
          "author": "답변자",
          "content": "@작성자 좋은 것 같아요!",
          "mentions": ["작성자"],
          "createdAt": "..."
        }
      ]
    }
  ],
  "totalCount": 5
}
```

---

## 4. 패키지 구조 (Vertical Slice)

```
com.jongwon.monad/
└── comment/
    ├── createcomment/
    │   ├── CreateCommentRequest.java     (record)
    │   ├── CreateCommentResponse.java    (record)
    │   ├── CreateCommentUseCase.java
    │   └── CreateCommentController.java
    ├── createreply/
    │   ├── CreateReplyRequest.java
    │   ├── CreateReplyResponse.java
    │   ├── CreateReplyUseCase.java
    │   └── CreateReplyController.java
    ├── listcomments/
    │   ├── ListCommentsResponse.java     (계층 구조: CommentItem + ReplyItem, mentions 포함)
    │   ├── ListCommentsUseCase.java
    │   └── ListCommentsController.java
    ├── updatecomment/
    │   ├── UpdateCommentRequest.java
    │   ├── UpdateCommentResponse.java
    │   ├── UpdateCommentUseCase.java
    │   └── UpdateCommentController.java
    ├── deletecomment/
    │   ├── DeleteCommentUseCase.java
    │   └── DeleteCommentController.java
    ├── domain/
    │   ├── Comment.java                  (Aggregate Root, 순수 Java)
    │   └── CommentRepository.java        (Port 인터페이스)
    └── infra/
        └── InMemoryCommentRepository.java
```

---

## 5. Port 인터페이스

```java
public interface CommentRepository {
    Comment save(Comment comment);
    Optional<Comment> findById(Long id);
    List<Comment> findAllByPostId(Long postId);
    long countByPostId(Long postId);
    void deleteById(Long id);
    void deleteAllByParentId(Long parentId);
}
```

- `findAllByPostId` — 해당 게시글의 모든 댓글+대댓글 조회 (UseCase에서 계층 구성)
- `deleteAllByParentId` — 부모 댓글 삭제 시 대댓글도 함께 삭제

---

## 6. 멘션(@태그) 기능 상세

### 6.1 멘션 파싱 규칙

- 패턴: `@닉네임` — 정규식 `@([가-힣a-zA-Z0-9_]{2,20})`
- 닉네임 규칙(2~20자, 한글/영문/숫자/밑줄)과 동일한 범위
- content 내 여러 멘션 가능, 중복은 제거
- 파싱은 Comment 도메인 내부에서 수행 (순수 Java 로직)

### 6.2 멘션 검증 (UseCase)

- content에서 파싱된 닉네임 목록에 대해 MemberRepository.existsByNickname()으로 존재 여부 확인
- **존재하지 않는 닉네임은 무시** (예외가 아님) — mentions 목록에서 제외
- 유효한 멘션만 Comment.mentions에 저장

### 6.3 멘션 흐름

```
1. 사용자가 content에 "@홍길동 좋은 글이네요" 입력
2. Comment.create() → content에서 ["홍길동"] 파싱
3. UseCase → MemberRepository.existsByNickname("홍길동") 확인
4. 존재하면 mentions = ["홍길동"], 없으면 mentions = []
5. Comment 저장
6. Response에 mentions 포함하여 반환
```

### 6.4 수정 시 멘션 재파싱

- Comment.update(content) 호출 시 새 content에서 멘션 재파싱
- UseCase에서 다시 존재 여부 검증 후 유효한 멘션만 저장

---

## 7. 테스트 전략

### 7.1 테스트 디렉토리 구조

```
src/test/java/com/jongwon/monad/
├── comment/
│   ├── domain/
│   │   └── CommentTest.java
│   ├── createcomment/
│   │   └── CreateCommentUseCaseTest.java
│   ├── createreply/
│   │   └── CreateReplyUseCaseTest.java
│   ├── listcomments/
│   │   └── ListCommentsUseCaseTest.java
│   ├── updatecomment/
│   │   └── UpdateCommentUseCaseTest.java
│   ├── deletecomment/
│   │   └── DeleteCommentUseCaseTest.java
│   └── fake/
│       └── FakeCommentRepository.java
│
└── fixture/
    └── CommentFixture.java
```

### 7.2 필수 테스트 목록

**Comment 도메인:**
- 정상 생성 (일반 댓글, parentId=null)
- 대댓글 정상 생성 (parentId 지정)
- content 빈 문자열 → 예외
- content null → 예외
- content 500자 초과 → 예외
- author 빈 문자열 → 예외
- postId null → 예외
- 댓글 수정 성공
- isReply() 동작 확인
- 멘션 파싱 — content에 @닉네임 포함 시 mentions 추출
- 멘션 파싱 — 멘션 없는 content면 빈 리스트
- 멘션 파싱 — 중복 멘션 제거
- 멘션 파싱 — 닉네임 규칙에 맞지 않는 @태그는 무시 (예: @a → 2자 미만)
- 수정 시 멘션 재파싱

**UseCase:**
- 댓글 작성 성공
- 존재하지 않는 게시글에 댓글 작성 → EntityNotFoundException
- 댓글 작성 시 멘션된 닉네임이 존재하면 mentions에 포함
- 댓글 작성 시 멘션된 닉네임이 존재하지 않으면 mentions에서 제외
- 대댓글 작성 성공
- 존재하지 않는 부모 댓글에 대댓글 작성 → EntityNotFoundException
- 대댓글에 대한 대댓글 시도 → IllegalArgumentException (depth 제한)
- 댓글 목록 조회 — 계층 구조 확인 (댓글 아래 대댓글, mentions 포함)
- 댓글 수정 성공 — 멘션 재검증 확인
- 존재하지 않는 댓글 수정 → 예외
- 댓글 삭제 성공 (대댓글 함께 삭제 확인)
- 존재하지 않는 댓글 삭제 → 예외

---

## 8. 구현 순서 (TDD 기반)

1. **Comment 도메인** — CommentTest 작성 → Comment 엔티티 구현 (멘션 파싱 포함)
2. **Comment Port** — CommentRepository 인터페이스 정의
3. **Comment Fake + Fixture** — FakeCommentRepository, CommentFixture 작성
4. **Comment InMemory Adapter** — InMemoryCommentRepository 구현
5. **Vertical Slices** — 각 슬라이스별 UseCase 테스트 → UseCase → Controller
   - CreateComment → CreateReply → ListComments → UpdateComment → DeleteComment
   - CreateComment, CreateReply, UpdateComment UseCase는 MemberRepository를 주입받아 멘션 검증
6. **전체 빌드 확인** — ./gradlew build 통과

---

## 9. 이번 범위에서 제외하는 항목

- 멘션 알림 (푸시/이메일 등) — 추후 알림 시스템 구현 시
- 댓글 좋아요
- 댓글 신고
- 대대댓글 (depth 2 이상)
- 댓글 작성자 = Member 연동 (추후 Post.author 전환과 함께)
- 댓글 페이징 (게시글당 댓글 수가 많지 않다는 전제)
