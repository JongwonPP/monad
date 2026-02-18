# 게시글 정렬 개발 계획

## 1. 목표

게시글 목록 조회 시 다양한 기준으로 정렬할 수 있는 기능을 구현한다.
기존 ListPosts 슬라이스를 확장하여 `sort` 파라미터를 추가한다.

### 설계 원칙

기존 도메인과 동일:
- Vertical Slice Architecture — 기존 ListPosts 슬라이스 확장
- DDD — Port 인터페이스에 정렬 파라미터 추가
- TDD — 정렬 동작 테스트 먼저 작성

---

## 2. 정렬 기준

| sort 값 | 설명 | 정렬 조건 |
|---------|------|----------|
| `latest` | 최신순 (기본값) | `created_at DESC` |
| `oldest` | 오래된순 | `created_at ASC` |
| `views` | 조회수순 | `view_count DESC, created_at DESC` |
| `likes` | 좋아요순 | `like_count DESC, created_at DESC` |

- `likes` 정렬은 좋아요 기능 구현 후 활성화 (좋아요 기능과 의존관계)
- 동일 값일 때는 최신순 2차 정렬

---

## 3. API 명세

### 3.1 게시글 목록 (기존 확장)

```
GET /api/v1/boards/{boardId}/posts?page=0&size=20&sort=latest
```

**인증**: 불필요

**Query Parameters** (변경사항):

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| page | number | X | 0 | 페이지 번호 |
| size | number | X | 20 | 페이지 크기 |
| sort | string | X | latest | 정렬 기준 (`latest`, `oldest`, `views`, `likes`) |

**Response** — 기존과 동일:
```json
{
  "posts": [
    {
      "id": 1,
      "title": "게시글 제목",
      "memberId": 1,
      "nickname": "홍길동",
      "viewCount": 42,
      "createdAt": "2025-01-15T10:30:00"
    }
  ],
  "totalCount": 150,
  "page": 0,
  "size": 20
}
```

**에러**:
- `400` — 유효하지 않은 sort 값

---

## 4. 도메인 모델

### 4.1 PostSortType (Enum)

```java
public enum PostSortType {
    LATEST,     // created_at DESC
    OLDEST,     // created_at ASC
    VIEWS,      // view_count DESC, created_at DESC
    LIKES;      // like_count DESC, created_at DESC

    public static PostSortType from(String value) {
        // null이면 LATEST, 잘못된 값이면 IllegalArgumentException
    }
}
```

- `post/domain/` 패키지에 위치
- 문자열 → enum 변환 시 대소문자 무시

---

## 5. 변경 범위

### 5.1 기존 코드 수정

이 기능은 기존 ListPosts 슬라이스를 확장한다 (새 슬라이스 생성 아님).

**ListPostsController**:
- `@RequestParam(defaultValue = "latest") String sort` 파라미터 추가

**ListPostsUseCase**:
- `execute(boardId, page, size, sortType)` 시그니처 변경

**PostRepository (Port)**:
- `findAllByBoardId(Long boardId, int page, int size, PostSortType sortType)` 시그니처 변경

**FakePostRepository**:
- 정렬 로직 추가 (Comparator 활용)

**JdbcPostRepository**:
- ORDER BY 절 동적 구성

### 5.2 패키지 구조

```
com.jongwon.monad/
└── post/
    ├── domain/
    │   ├── Post.java
    │   ├── PostRepository.java      ← 시그니처 변경
    │   └── PostSortType.java        ← 신규
    └── listposts/
        ├── ListPostsController.java  ← sort 파라미터 추가
        ├── ListPostsResponse.java    (변경 없음)
        └── ListPostsUseCase.java     ← sortType 전달
```

---

## 6. Port 인터페이스 변경

### PostRepository

```java
// 기존
List<Post> findAllByBoardId(Long boardId, int page, int size);

// 변경
List<Post> findAllByBoardId(Long boardId, int page, int size, PostSortType sortType);
```

- 기존 메서드 시그니처를 직접 변경 (오버로딩 아님)
- 모든 어댑터(Fake, JDBC)에서 정렬 지원

---

## 7. 테스트 전략

### 7.1 테스트 파일

```
src/test/java/com/jongwon/monad/
└── post/
    ├── domain/
    │   └── PostSortTypeTest.java        ← 신규
    └── listposts/
        └── ListPostsUseCaseTest.java    ← 정렬 테스트 추가
```

### 7.2 필수 테스트 목록

**PostSortType 도메인:**
- `from("latest")` → LATEST
- `from("VIEWS")` → VIEWS (대소문자 무시)
- `from(null)` → LATEST (기본값)
- `from("invalid")` → IllegalArgumentException

**ListPosts UseCase (추가 테스트):**
- 기본 정렬(sort 미지정) — 최신순으로 반환
- `latest` 정렬 — createdAt 내림차순
- `oldest` 정렬 — createdAt 오름차순
- `views` 정렬 — viewCount 내림차순, 동일 시 최신순
- `likes` 정렬 — likeCount 내림차순, 동일 시 최신순 (좋아요 기능 구현 후)

---

## 8. 구현 순서 (TDD 기반)

1. **PostSortType** — enum 정의 + PostSortTypeTest 작성
2. **PostRepository** — 메서드 시그니처 변경
3. **FakePostRepository** — 정렬 로직 구현 (Comparator)
4. **ListPostsUseCaseTest** — 정렬 테스트 추가
5. **ListPostsUseCase** — sortType 파라미터 전달
6. **ListPostsController** — sort 쿼리 파라미터 추가
7. **JdbcPostRepository** — ORDER BY 동적 구성
8. **빌드 확인** — `./gradlew build` 통과

---

## 9. likes 정렬 의존관계

`likes` 정렬은 좋아요(likes) 기능에 의존한다.

- 좋아요 기능 구현 전: `PostSortType.LIKES` 선택 시 `LATEST`로 fallback하거나 예외 처리
- 좋아요 기능 구현 후: `PostLikeRepository.countByPostId()` 활용하여 정렬
- JDBC에서는 `post_like` 테이블과 JOIN 또는 서브쿼리로 구현

**권장 구현 순서**: 좋아요 기능 먼저 구현 → 정렬에서 `likes` 옵션 활성화

---

## 10. 이번 범위에서 제외

- 댓글 수 기준 정렬 (추후)
- 복합 정렬 (예: sort=views,latest)
- 검색 결과의 정렬 (검색 기능에서 별도 처리)
