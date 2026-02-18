# 내가 쓴 글/댓글 조회 (마이페이지) 개발 계획

## 1. 목표

인증된 사용자가 자신이 작성한 게시글과 댓글을 조회할 수 있는 기능을 구현한다.
JWT 토큰에서 memberId를 추출하여 해당 회원의 게시글/댓글만 반환한다.

### 설계 원칙

기존 도메인과 동일:
- Vertical Slice Architecture
- DDD — Port 인터페이스에 메서드 추가
- TDD — 테스트 먼저 작성
- DB 비의존 — Fake + JDBC 어댑터

---

## 2. API 명세

### 2.1 내가 쓴 글 조회

```
GET /api/v1/members/me/posts?page=0&size=20
```

**인증**: 필요

**Query Parameters**:

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| page | number | X | 0 | 페이지 번호 (0부터) |
| size | number | X | 20 | 페이지 크기 |

**Response** `200 OK`:
```json
{
  "posts": [
    {
      "id": 1,
      "boardId": 1,
      "boardName": "자유게시판",
      "title": "내가 쓴 게시글",
      "viewCount": 42,
      "createdAt": "2025-01-15T10:30:00"
    }
  ],
  "totalCount": 8,
  "page": 0,
  "size": 20
}
```

- 최신순 정렬 (createdAt DESC)
- `boardName` 포함 — 어느 게시판에 쓴 글인지 표시
- `memberId`, `nickname`은 자신의 글이므로 불필요

---

### 2.2 내가 쓴 댓글 조회

```
GET /api/v1/members/me/comments?page=0&size=20
```

**인증**: 필요

**Query Parameters**:

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| page | number | X | 0 | 페이지 번호 (0부터) |
| size | number | X | 20 | 페이지 크기 |

**Response** `200 OK`:
```json
{
  "comments": [
    {
      "id": 5,
      "postId": 1,
      "postTitle": "원본 게시글 제목",
      "content": "내가 쓴 댓글 내용",
      "mentions": ["홍길동"],
      "createdAt": "2025-01-15T10:35:00"
    }
  ],
  "totalCount": 12,
  "page": 0,
  "size": 20
}
```

- 최신순 정렬 (createdAt DESC)
- `postTitle` 포함 — 어떤 게시글에 달린 댓글인지 표시
- 대댓글도 포함 (댓글/대댓글 구분 없이 flat 목록)

---

## 3. 패키지 구조 (Vertical Slice)

```
com.jongwon.monad/
├── post/
│   └── myposts/                       ← 신규 슬라이스
│       ├── MyPostsResponse.java
│       ├── MyPostsUseCase.java
│       └── MyPostsController.java
│
└── comment/
    └── mycomments/                    ← 신규 슬라이스
        ├── MyCommentsResponse.java
        ├── MyCommentsUseCase.java
        └── MyCommentsController.java
```

---

## 4. Port 인터페이스 변경

### PostRepository — 메서드 추가

```java
List<Post> findAllByMemberId(Long memberId, int page, int size);
long countByMemberId(Long memberId);
```

- 최신순 정렬 (created_at DESC)

### CommentRepository — 메서드 추가

```java
List<Comment> findAllByMemberId(Long memberId, int page, int size);
long countByMemberId(Long memberId);
```

- 최신순 정렬 (created_at DESC)
- 댓글과 대댓글 구분 없이 해당 회원이 작성한 모든 댓글 반환

---

## 5. 테스트 전략

### 5.1 테스트 디렉토리

```
src/test/java/com/jongwon/monad/
├── post/
│   └── myposts/
│       └── MyPostsUseCaseTest.java
└── comment/
    └── mycomments/
        └── MyCommentsUseCaseTest.java
```

### 5.2 필수 테스트 목록

**MyPosts UseCase:**
- 내가 쓴 글 조회 성공 — 본인 글만 반환
- 다른 회원의 글은 포함되지 않음
- 최신순 정렬 확인
- 페이징 동작 확인
- 작성한 글이 없으면 빈 리스트 반환
- boardName 포함 확인

**MyComments UseCase:**
- 내가 쓴 댓글 조회 성공 — 본인 댓글만 반환
- 댓글 + 대댓글 모두 포함
- 다른 회원의 댓글은 포함되지 않음
- 최신순 정렬 확인
- 페이징 동작 확인
- 작성한 댓글이 없으면 빈 리스트 반환
- postTitle 포함 확인

---

## 6. 구현 순서 (TDD 기반)

### Phase 1: 내가 쓴 글

1. **PostRepository** — `findAllByMemberId`, `countByMemberId` 메서드 추가
2. **FakePostRepository** — 메서드 구현
3. **MyPostsUseCaseTest** — 테스트 작성
4. **MyPostsUseCase** — 구현 (PostRepository + BoardRepository 주입)
5. **MyPostsResponse** — record 정의
6. **MyPostsController** — `GET /api/v1/members/me/posts`

### Phase 2: 내가 쓴 댓글

7. **CommentRepository** — `findAllByMemberId`, `countByMemberId` 메서드 추가
8. **FakeCommentRepository** — 메서드 구현
9. **MyCommentsUseCaseTest** — 테스트 작성
10. **MyCommentsUseCase** — 구현 (CommentRepository + PostRepository 주입)
11. **MyCommentsResponse** — record 정의
12. **MyCommentsController** — `GET /api/v1/members/me/comments`

### Phase 3: DB 연동 + 마무리

13. **JdbcPostRepository** — findAllByMemberId 구현
14. **JdbcCommentRepository** — findAllByMemberId 구현
15. **SecurityConfig** — `/api/v1/members/me/**` 인증 필수 설정
16. **빌드 확인** — `./gradlew build` 통과

---

## 7. DB (Flyway 마이그레이션)

테이블 변경 없음. 기존 `post.member_id`, `comment.member_id` 컬럼으로 조회한다.

성능 개선을 위한 인덱스 추가 (선택적):
```sql
-- V3__add_member_id_indexes.sql (필요 시)
CREATE INDEX idx_post_member_id ON post(member_id);
CREATE INDEX idx_comment_member_id ON comment(member_id);
```

---

## 8. 인증 처리

- Controller에서 `@AuthenticationPrincipal`로 memberId 추출
- 기존 Post/Comment의 인증 패턴과 동일
- `/api/v1/members/me/*` 경로는 인증 필수

```java
@GetMapping("/api/v1/members/me/posts")
public ResponseEntity<MyPostsResponse> myPosts(
        @AuthenticationPrincipal AuthenticationPrincipal principal,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    MyPostsResponse response = myPostsUseCase.execute(principal.memberId(), page, size);
    return ResponseEntity.ok(response);
}
```

---

## 9. 이번 범위에서 제외

- 내가 좋아요한 글 목록
- 내가 북마크한 글 목록
- 활동 이력 (타임라인)
- 통계 (총 글 수, 받은 좋아요 수 등)
