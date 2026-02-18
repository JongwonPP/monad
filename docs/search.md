# 게시글 검색 개발 계획

## 1. 목표

게시글 제목/본문을 키워드로 검색할 수 있는 기능을 구현한다.
게시판(Board) 범위 지정은 선택적이며, 전체 검색도 가능하다.

### 설계 원칙

기존 도메인과 동일:
- Vertical Slice Architecture
- DDD — 순수 Java 도메인, Port 인터페이스
- TDD — 테스트 먼저 작성
- DB 비의존 — Fake + JDBC 어댑터

---

## 2. API 명세

### 2.1 게시글 검색

```
GET /api/v1/posts/search?keyword=검색어&boardId=1&page=0&size=20
```

**인증**: 불필요

**Query Parameters**:

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| keyword | string | O | — | 검색 키워드 (최소 1자) |
| boardId | number | X | — | 게시판 ID (미지정 시 전체 게시판 검색) |
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
      "title": "Spring Boot 검색 구현",
      "memberId": 1,
      "nickname": "홍길동",
      "viewCount": 42,
      "createdAt": "2025-01-15T10:30:00"
    }
  ],
  "totalCount": 5,
  "page": 0,
  "size": 20
}
```

- 제목과 본문을 **모두** 검색한다 (OR 조건 — 제목에 포함 OR 본문에 포함)
- 검색 결과에 `boardName`을 포함하여 어느 게시판의 글인지 알 수 있게 한다
- 결과는 최신순(createdAt DESC)으로 정렬

**에러**:
- `400` — keyword가 빈 값이거나 누락

---

## 3. 패키지 구조 (Vertical Slice)

```
com.jongwon.monad/
└── post/
    └── searchposts/
        ├── SearchPostsRequest.java      (keyword, boardId, page, size 바인딩용)
        ├── SearchPostsResponse.java     (record — PostItem + boardName 포함)
        ├── SearchPostsUseCase.java
        └── SearchPostsController.java
```

기존 `post/domain/`에 Port 메서드만 추가하고, 새 슬라이스는 `searchposts/`로 분리한다.

---

## 4. Port 인터페이스 변경

### PostRepository — 메서드 추가

```java
// 전체 게시판 검색
List<Post> searchByKeyword(String keyword, int page, int size);
long countByKeyword(String keyword);

// 게시판 범위 검색
List<Post> searchByBoardIdAndKeyword(Long boardId, String keyword, int page, int size);
long countByBoardIdAndKeyword(Long boardId, String keyword);
```

- keyword는 `title LIKE %keyword% OR content LIKE %keyword%` 조건
- 결과는 `created_at DESC` 정렬
- Fake 어댑터에서는 `String.contains()`로 구현

---

## 5. 테스트 전략

### 5.1 테스트 디렉토리

```
src/test/java/com/jongwon/monad/
└── post/
    └── searchposts/
        └── SearchPostsUseCaseTest.java
```

### 5.2 필수 테스트 목록

**UseCase:**
- 키워드로 제목 검색 — 제목에 키워드가 포함된 게시글 반환
- 키워드로 본문 검색 — 본문에 키워드가 포함된 게시글 반환
- 제목+본문 동시 매칭 — 중복 없이 반환
- 게시판 범위 지정 검색 — boardId 지정 시 해당 게시판만 검색
- 전체 검색 — boardId 미지정 시 모든 게시판 검색
- 검색 결과 없음 — 빈 리스트 반환 (에러 아님)
- 페이징 동작 확인
- 빈 키워드 → IllegalArgumentException

---

## 6. 구현 순서 (TDD 기반)

1. **PostRepository** — 검색 메서드 4개 추가 (인터페이스)
2. **FakePostRepository** — 검색 메서드 구현 (String.contains)
3. **SearchPostsUseCaseTest** — 테스트 작성
4. **SearchPostsUseCase** — 구현 (PostRepository + MemberRepository + BoardRepository 주입)
5. **SearchPostsResponse** — record 정의
6. **SearchPostsController** — `GET /api/v1/posts/search` 엔드포인트
7. **JdbcPostRepository** — 검색 메서드 구현 (LIKE 쿼리)
8. **SecurityConfig** — `/api/v1/posts/search` permitAll 추가
9. **빌드 확인** — `./gradlew build` 통과

---

## 7. DB (Flyway 마이그레이션)

테이블 변경 없음. 기존 `post` 테이블의 `title`, `content` 컬럼으로 검색한다.

선택적으로 검색 성능 개선을 위한 인덱스 추가 가능 (추후):
```sql
-- V3__add_search_index.sql (필요 시)
CREATE INDEX idx_post_title ON post(title);
```

---

## 8. 이번 범위에서 제외

- 전문 검색(Full-text Search) — LIKE 검색으로 충분
- 검색 자동완성
- 검색어 하이라이팅
- 검색 히스토리
