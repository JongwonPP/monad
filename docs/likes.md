# 좋아요(추천) 기능 개발 계획

## 1. 목표

게시글과 댓글에 좋아요(추천) 기능을 구현한다.
회원당 하나의 좋아요만 가능하며, 좋아요/취소를 명시적으로 분리한다.

### 설계 원칙

기존 도메인과 동일:
- Vertical Slice Architecture
- DDD — 순수 Java 도메인 엔티티, Port 인터페이스
- TDD — 테스트 먼저 작성
- DB 비의존 — Fake + JDBC 어댑터

---

## 2. 도메인 모델

### 2.1 PostLike

| 필드 | 타입 | 제약 조건 | 설명 |
|------|------|----------|------|
| id | Long | PK | 식별자 |
| postId | Long | NOT NULL | 게시글 ID |
| memberId | Long | NOT NULL | 회원 ID |
| createdAt | LocalDateTime | | 좋아요 시각 |

**도메인 규칙:**
- postId, memberId: null 불가
- 동일 게시글에 동일 회원의 좋아요는 1개만 가능 (UseCase에서 중복 검증)

**메서드:**
- `PostLike.create(postId, memberId)` — 정적 팩토리
- `PostLike.reconstruct(id, postId, memberId, createdAt)` — DB 복원

### 2.2 CommentLike

| 필드 | 타입 | 제약 조건 | 설명 |
|------|------|----------|------|
| id | Long | PK | 식별자 |
| commentId | Long | NOT NULL | 댓글 ID |
| memberId | Long | NOT NULL | 회원 ID |
| createdAt | LocalDateTime | | 좋아요 시각 |

**도메인 규칙:**
- commentId, memberId: null 불가
- 동일 댓글에 동일 회원의 좋아요는 1개만 가능

**메서드:**
- `CommentLike.create(commentId, memberId)` — 정적 팩토리
- `CommentLike.reconstruct(id, commentId, memberId, createdAt)` — DB 복원

---

## 3. API 명세

### 3.1 게시글 좋아요

```
POST /api/v1/posts/{postId}/likes
```

**인증**: 필요

**Request Body**: 없음

**Response** `200 OK`:
```json
{
  "postId": 1,
  "liked": true,
  "likeCount": 43
}
```

**에러**:
- `400` — 이미 좋아요한 게시글
- `404` — 게시글 없음

---

### 3.2 게시글 좋아요 취소

```
DELETE /api/v1/posts/{postId}/likes
```

**인증**: 필요

**Response** `200 OK`:
```json
{
  "postId": 1,
  "liked": false,
  "likeCount": 42
}
```

**에러**:
- `400` — 좋아요하지 않은 게시글
- `404` — 게시글 없음

---

### 3.3 댓글 좋아요

```
POST /api/v1/comments/{commentId}/likes
```

**인증**: 필요

**Request Body**: 없음

**Response** `200 OK`:
```json
{
  "commentId": 1,
  "liked": true,
  "likeCount": 5
}
```

**에러**:
- `400` — 이미 좋아요한 댓글
- `404` — 댓글 없음

---

### 3.4 댓글 좋아요 취소

```
DELETE /api/v1/comments/{commentId}/likes
```

**인증**: 필요

**Response** `200 OK`:
```json
{
  "commentId": 1,
  "liked": false,
  "likeCount": 4
}
```

**에러**:
- `400` — 좋아요하지 않은 댓글
- `404` — 댓글 없음

---

## 4. 기존 응답 변경

### 4.1 게시글 목록 (ListPosts) — likeCount 추가

```json
{
  "posts": [
    {
      "id": 1,
      "title": "게시글 제목",
      "memberId": 1,
      "nickname": "홍길동",
      "viewCount": 42,
      "likeCount": 10,
      "createdAt": "2025-01-15T10:30:00"
    }
  ],
  "totalCount": 150,
  "page": 0,
  "size": 20
}
```

### 4.2 게시글 상세 (GetPost) — likeCount, liked 추가

```json
{
  "id": 1,
  "boardId": 1,
  "title": "게시글 제목",
  "content": "본문",
  "memberId": 1,
  "nickname": "홍길동",
  "viewCount": 43,
  "likeCount": 10,
  "liked": true,
  "createdAt": "2025-01-15T10:30:00"
}
```

- `liked`: 현재 인증된 사용자가 좋아요했는지 (미인증 시 `false`)

### 4.3 댓글 목록 (ListComments) — likeCount 추가

```json
{
  "comments": [
    {
      "id": 1,
      "memberId": 1,
      "nickname": "홍길동",
      "content": "댓글 내용",
      "mentions": [],
      "likeCount": 3,
      "createdAt": "2025-01-15T10:30:00",
      "replies": [
        {
          "id": 3,
          "memberId": 2,
          "nickname": "김철수",
          "content": "대댓글",
          "mentions": [],
          "likeCount": 1,
          "createdAt": "2025-01-15T10:35:00"
        }
      ]
    }
  ],
  "totalCount": 3
}
```

---

## 5. 패키지 구조

```
com.jongwon.monad/
├── post/
│   ├── domain/
│   │   ├── PostLike.java              ← 신규
│   │   └── PostLikeRepository.java    ← 신규 Port
│   ├── infra/
│   │   ├── FakePostLikeRepository.java   ← 신규 (@Profile local)
│   │   └── JdbcPostLikeRepository.java   ← 신규 (@Profile prod)
│   ├── likepost/                      ← 신규 슬라이스
│   │   ├── LikePostResponse.java
│   │   ├── LikePostUseCase.java
│   │   └── LikePostController.java
│   └── unlikepost/                    ← 신규 슬라이스
│       ├── UnlikePostResponse.java
│       ├── UnlikePostUseCase.java
│       └── UnlikePostController.java
│
└── comment/
    ├── domain/
    │   ├── CommentLike.java           ← 신규
    │   └── CommentLikeRepository.java ← 신규 Port
    ├── infra/
    │   ├── FakeCommentLikeRepository.java  ← 신규 (@Profile local)
    │   └── JdbcCommentLikeRepository.java  ← 신규 (@Profile prod)
    ├── likecomment/                   ← 신규 슬라이스
    │   ├── LikeCommentResponse.java
    │   ├── LikeCommentUseCase.java
    │   └── LikeCommentController.java
    └── unlikecomment/                 ← 신규 슬라이스
        ├── UnlikeCommentResponse.java
        ├── UnlikeCommentUseCase.java
        └── UnlikeCommentController.java
```

---

## 6. Port 인터페이스

### PostLikeRepository

```java
public interface PostLikeRepository {
    PostLike save(PostLike postLike);
    Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);
    long countByPostId(Long postId);
    void deleteByPostIdAndMemberId(Long postId, Long memberId);
}
```

### CommentLikeRepository

```java
public interface CommentLikeRepository {
    CommentLike save(CommentLike commentLike);
    Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId);
    long countByCommentId(Long commentId);
    void deleteByCommentIdAndMemberId(Long commentId, Long memberId);
}
```

---

## 7. 테스트 전략

### 7.1 테스트 디렉토리

```
src/test/java/com/jongwon/monad/
├── post/
│   ├── domain/
│   │   └── PostLikeTest.java
│   ├── likepost/
│   │   └── LikePostUseCaseTest.java
│   └── unlikepost/
│       └── UnlikePostUseCaseTest.java
├── comment/
│   ├── domain/
│   │   └── CommentLikeTest.java
│   ├── likecomment/
│   │   └── LikeCommentUseCaseTest.java
│   └── unlikecomment/
│       └── UnlikeCommentUseCaseTest.java
└── fixture/
    ├── PostLikeFixture.java
    └── CommentLikeFixture.java
```

### 7.2 필수 테스트 목록

**PostLike 도메인:**
- 정상 생성
- postId null → 예외
- memberId null → 예외

**LikePost UseCase:**
- 게시글 좋아요 성공
- 존재하지 않는 게시글 → EntityNotFoundException
- 이미 좋아요한 게시글 → IllegalArgumentException
- 좋아요 후 likeCount 증가 확인

**UnlikePost UseCase:**
- 게시글 좋아요 취소 성공
- 존재하지 않는 게시글 → EntityNotFoundException
- 좋아요하지 않은 게시글 취소 시도 → IllegalArgumentException
- 취소 후 likeCount 감소 확인

**CommentLike 도메인:**
- 정상 생성
- commentId null → 예외
- memberId null → 예외

**LikeComment UseCase:**
- 댓글 좋아요 성공
- 존재하지 않는 댓글 → EntityNotFoundException
- 이미 좋아요한 댓글 → IllegalArgumentException

**UnlikeComment UseCase:**
- 댓글 좋아요 취소 성공
- 존재하지 않는 댓글 → EntityNotFoundException
- 좋아요하지 않은 댓글 취소 시도 → IllegalArgumentException

---

## 8. DB (Flyway 마이그레이션)

### V2__add_like_tables.sql

```sql
CREATE TABLE post_like (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT    NOT NULL,
    member_id  BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,
    UNIQUE (post_id, member_id)
);

CREATE TABLE comment_like (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT    NOT NULL,
    member_id  BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,
    UNIQUE (comment_id, member_id)
);

CREATE INDEX idx_post_like_post_id ON post_like(post_id);
CREATE INDEX idx_comment_like_comment_id ON comment_like(comment_id);
```

- `UNIQUE (post_id, member_id)` — DB 레벨에서도 중복 좋아요 방지
- 인덱스 — likeCount 조회 성능 확보

---

## 9. 구현 순서 (TDD 기반)

### Phase 1: 게시글 좋아요

1. **PostLike 도메인** — PostLikeTest 작성 → PostLike 엔티티 구현
2. **PostLikeRepository** — Port 인터페이스 정의
3. **FakePostLikeRepository** — InMemory 구현
4. **PostLikeFixture** — 테스트 픽스처
5. **LikePost 슬라이스** — LikePostUseCaseTest → UseCase → Controller
6. **UnlikePost 슬라이스** — UnlikePostUseCaseTest → UseCase → Controller

### Phase 2: 댓글 좋아요

7. **CommentLike 도메인** — CommentLikeTest → CommentLike 엔티티
8. **CommentLikeRepository** — Port 인터페이스
9. **FakeCommentLikeRepository** — InMemory 구현
10. **CommentLikeFixture** — 테스트 픽스처
11. **LikeComment 슬라이스** — UseCase 테스트 → UseCase → Controller
12. **UnlikeComment 슬라이스** — UseCase 테스트 → UseCase → Controller

### Phase 3: 기존 응답 통합

13. **ListPosts** — likeCount 추가 (Response + UseCase 수정)
14. **GetPost** — likeCount, liked 추가 (Response + UseCase 수정)
15. **ListComments** — likeCount 추가 (Response + UseCase 수정)

### Phase 4: DB 연동

16. **Flyway 마이그레이션** — V2__add_like_tables.sql
17. **JdbcPostLikeRepository** — JDBC 구현
18. **JdbcCommentLikeRepository** — JDBC 구현
19. **SecurityConfig** — likes 엔드포인트 인증 설정
20. **빌드 확인** — `./gradlew build` 통과

---

## 10. 이번 범위에서 제외

- 좋아요 취소 불가 정책 (한번 누르면 취소 불가)
- 좋아요 알림
- 좋아요 누른 회원 목록 조회
- 싫어요(비추천) 기능
