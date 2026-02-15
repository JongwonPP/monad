# 게시판 CRUD 개발 계획

## 1. 목표

게시판(Board)과 게시글(Post)에 대한 기본 CRUD REST API를 구현한다.
인증/인가는 이번 범위에 포함하지 않으며, 순수하게 게시판 데이터 조작에 집중한다.

### 핵심 설계 원칙

- **Vertical Slice Architecture** — 기술 계층이 아닌 기능(Use Case) 단위로 코드를 조직한다
- **DDD (Domain-Driven Design)** — 도메인 모델에 비즈니스 로직을 집중하고, 인프라에 의존하지 않는 순수한 도메인을 유지한다
- **TDD (Test-Driven Development)** — 중요한 비즈니스 로직은 반드시 테스트를 먼저 작성한 뒤 구현한다
- **DB 비의존적 구조** — Port(인터페이스)를 통해 영속성을 추상화하여, 어떤 DB로든 교체 가능한 구조를 만든다

---

## 2. 아키텍처

### 2.1 Vertical Slice Architecture

기존 레이어드 아키텍처(controller → service → repository)가 아닌,
**기능(Use Case) 단위**로 슬라이스를 나눈다. 각 슬라이스는 독립적이며 자기 완결적이다.

```
Board 도메인 예시:

board/
├── CreateBoard/          ← 슬라이스 1
│   ├── CreateBoardRequest.java
│   ├── CreateBoardResponse.java
│   ├── CreateBoardUseCase.java
│   └── CreateBoardController.java
├── GetBoard/             ← 슬라이스 2
├── ListBoards/           ← 슬라이스 3
├── UpdateBoard/          ← 슬라이스 4
├── DeleteBoard/          ← 슬라이스 5
├── domain/               ← 도메인 모델 (슬라이스 간 공유)
│   ├── Board.java
│   └── BoardRepository.java   (Port 인터페이스)
└── infra/                ← 인프라 어댑터
    └── BoardRepositoryImpl.java
```

**원칙:**
- 각 슬라이스는 자신만의 Request/Response DTO, UseCase, Controller를 갖는다
- 도메인 모델과 Port 인터페이스는 슬라이스 간 공유한다
- 슬라이스 간 직접 의존을 피한다

### 2.2 DDD 관점의 계층 분리

```
┌──────────────────────────────────────┐
│  Controller (HTTP 어댑터)              │  ← 외부 요청 수신
├──────────────────────────────────────┤
│  UseCase (응용 서비스)                  │  ← 흐름 조율, 트랜잭션 경계
├──────────────────────────────────────┤
│  Domain (엔티티, 값 객체, 도메인 서비스)   │  ← 비즈니스 규칙 (순수 Java, 프레임워크 무의존)
├──────────────────────────────────────┤
│  Port (Repository 인터페이스)           │  ← 도메인이 정의하는 추상화
├──────────────────────────────────────┤
│  Infra (Adapter 구현체)               │  ← JPA, JDBC, 외부 API 등 교체 가능
└──────────────────────────────────────┘
```

- **Domain**: 프레임워크 의존 없음. 순수 Java 객체로 비즈니스 로직을 표현
- **Port**: 도메인 패키지에 위치하는 인터페이스. 도메인이 필요로 하는 영속성 계약을 정의
- **Infra(Adapter)**: Port의 구현체. JPA든 JDBC든 MongoDB든 여기서 교체

### 2.3 DB 비의존적 설계

```java
// domain 패키지 — Port 인터페이스
public interface BoardRepository {
    Board save(Board board);
    Optional<Board> findById(Long id);
    List<Board> findAll();
    void deleteById(Long id);
}

// infra 패키지 — 실제 구현 (추후 DB 선택 시)
public class JpaBoardRepository implements BoardRepository { ... }

// test — Fake 구현
public class FakeBoardRepository implements BoardRepository { ... }
```

- 현재 DB가 미정이므로, 도메인과 UseCase는 Port에만 의존
- 테스트 시에는 Fake 구현체 사용
- DB 결정 후 Adapter 구현체만 추가하면 됨

---

## 3. 사전 작업 — 의존성 및 설정

### 3.1 build.gradle.kts 의존성 추가

```kotlin
// DB 결정 전 최소 구성
compileOnly("org.projectlombok:lombok")
annotationProcessor("org.projectlombok:lombok")

// DB 결정 후 추가할 의존성 (예시)
// implementation("org.springframework.boot:spring-boot-starter-data-jpa")
// runtimeOnly("com.h2database:h2")
```

### 3.2 application.yml

```yaml
spring:
  application:
    name: monad

server:
  port: 8080

# DB 설정은 DB 선택 후 추가
```

---

## 4. 도메인 모델 설계

### 4.1 Board (Aggregate Root)

| 필드          | 타입            | 제약 조건          | 설명       |
|-------------|---------------|----------------|----------|
| id          | Long          | PK             | 식별자      |
| name        | String        | NOT NULL, UNIQUE | 게시판 이름   |
| description | String        |                | 게시판 설명   |
| createdAt   | LocalDateTime |                | 생성 시각    |
| updatedAt   | LocalDateTime |                | 수정 시각    |

**도메인 규칙:**
- name은 빈 문자열 불가, 최대 50자
- 생성 시 name 중복 검증은 UseCase에서 Repository를 통해 수행

### 4.2 Post (Aggregate Root)

| 필드        | 타입            | 제약 조건        | 설명                       |
|-----------|---------------|--------------|--------------------------|
| id        | Long          | PK           | 식별자                      |
| boardId   | Long          | NOT NULL     | 소속 게시판 ID (FK가 아닌 ID 참조) |
| title     | String        | NOT NULL     | 게시글 제목                   |
| content   | String        | NOT NULL     | 게시글 본문                   |
| author    | String        | NOT NULL     | 작성자 (추후 Member ID로 전환)   |
| viewCount | int           | default 0    | 조회수                      |
| createdAt | LocalDateTime |              | 생성 시각                    |
| updatedAt | LocalDateTime |              | 수정 시각                    |

**도메인 규칙:**
- title은 빈 문자열 불가, 최대 200자
- content는 빈 문자열 불가
- viewCount는 0 이상
- `increaseViewCount()` — 조회수 증가 메서드를 도메인에 둔다
- `update(title, content)` — 수정 메서드를 도메인에 둔다

> Post는 Board와 ID 참조로만 연결한다 (JPA 연관관계 매핑 없음).
> DB 비의존적 설계를 위해 객체 참조 대신 ID 참조를 사용한다.

---

## 5. API 명세

### 5.1 Board API — `/api/v1/boards`

| Method | Endpoint              | 설명        | Request Body            | Response       |
|--------|-----------------------|-----------|------------------------|----------------|
| POST   | `/api/v1/boards`      | 게시판 생성    | `{ name, description }` | 201 Created    |
| GET    | `/api/v1/boards`      | 게시판 목록 조회 | —                      | 200 OK, List   |
| GET    | `/api/v1/boards/{id}` | 게시판 단건 조회 | —                      | 200 OK         |
| PUT    | `/api/v1/boards/{id}` | 게시판 수정    | `{ name, description }` | 200 OK         |
| DELETE | `/api/v1/boards/{id}` | 게시판 삭제    | —                      | 204 No Content |

### 5.2 Post API — `/api/v1/boards/{boardId}/posts`

| Method | Endpoint                                  | 설명        | Request Body                 | Response       |
|--------|-------------------------------------------|-----------|------------------------------|----------------|
| POST   | `/api/v1/boards/{boardId}/posts`          | 게시글 작성    | `{ title, content, author }` | 201 Created    |
| GET    | `/api/v1/boards/{boardId}/posts`          | 게시글 목록 조회 | ?page=0&size=20              | 200 OK, Page   |
| GET    | `/api/v1/boards/{boardId}/posts/{postId}` | 게시글 단건 조회 | —                            | 200 OK         |
| PUT    | `/api/v1/boards/{boardId}/posts/{postId}` | 게시글 수정    | `{ title, content }`         | 200 OK         |
| DELETE | `/api/v1/boards/{boardId}/posts/{postId}` | 게시글 삭제    | —                            | 204 No Content |

---

## 6. 패키지 구조 (Vertical Slice)

```
com.jongwon.monad/
├── board/
│   ├── CreateBoard/
│   │   ├── CreateBoardRequest.java      (record)
│   │   ├── CreateBoardResponse.java     (record)
│   │   ├── CreateBoardUseCase.java
│   │   └── CreateBoardController.java
│   ├── GetBoard/
│   │   ├── GetBoardResponse.java
│   │   ├── GetBoardUseCase.java
│   │   └── GetBoardController.java
│   ├── ListBoards/
│   │   ├── ListBoardsResponse.java
│   │   ├── ListBoardsUseCase.java
│   │   └── ListBoardsController.java
│   ├── UpdateBoard/
│   │   ├── UpdateBoardRequest.java
│   │   ├── UpdateBoardResponse.java
│   │   ├── UpdateBoardUseCase.java
│   │   └── UpdateBoardController.java
│   ├── DeleteBoard/
│   │   ├── DeleteBoardUseCase.java
│   │   └── DeleteBoardController.java
│   ├── domain/
│   │   ├── Board.java                   (Aggregate Root, 순수 Java)
│   │   └── BoardRepository.java         (Port 인터페이스)
│   └── infra/
│       └── (DB 결정 후 구현)
│
├── post/
│   ├── CreatePost/
│   ├── GetPost/
│   ├── ListPosts/
│   ├── UpdatePost/
│   ├── DeletePost/
│   ├── domain/
│   │   ├── Post.java
│   │   └── PostRepository.java
│   └── infra/
│
└── global/
    └── exception/
        ├── EntityNotFoundException.java
        └── GlobalExceptionHandler.java
```

---

## 7. 테스트 전략

### 7.1 TDD 사이클

모든 비즈니스 로직은 아래 사이클로 개발한다:
1. **Red** — 실패하는 테스트를 먼저 작성
2. **Green** — 테스트를 통과시키는 최소 구현
3. **Refactor** — 코드 정리

### 7.2 테스트 분류

| 종류            | 대상                      | 인프라 의존 | 실행 속도 |
|---------------|-------------------------|--------|-------|
| 도메인 단위 테스트    | Board, Post 도메인 로직      | 없음     | 매우 빠름 |
| UseCase 단위 테스트 | UseCase 로직              | Fake   | 빠름    |
| E2E 로컬 테스트    | Controller → UseCase → Fake | Fake   | 빠름    |

### 7.3 Fake 객체와 Port 인터페이스

인프라(DB) 계층은 Port 인터페이스 뒤에 숨기고, 테스트에서는 Fake 구현체를 사용한다.
Fake는 실제 DB 없이 인메모리로 동작하므로 테스트가 빠르고 독립적이다.

```java
// Fake 구현 예시
public class FakeBoardRepository implements BoardRepository {
    private final Map<Long, Board> store = new HashMap<>();
    private long sequence = 0L;

    @Override
    public Board save(Board board) {
        if (board.getId() == null) {
            board.assignId(++sequence);
        }
        store.put(board.getId(), board);
        return board;
    }

    @Override
    public Optional<Board> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    // ...
}
```

### 7.4 TestFixture

테스트 데이터 생성을 일관성 있게 관리하기 위해 TestFixture를 사용한다.

```java
public class BoardFixture {
    public static Board create() {
        return Board.create("자유게시판", "자유롭게 글을 작성하세요");
    }

    public static Board createWithName(String name) {
        return Board.create(name, name + " 설명");
    }
}

public class PostFixture {
    public static Post create(Long boardId) {
        return Post.create(boardId, "테스트 제목", "테스트 본문", "작성자");
    }

    public static Post createWithTitle(Long boardId, String title) {
        return Post.create(boardId, title, "본문 내용", "작성자");
    }
}
```

### 7.5 테스트 디렉토리 구조

```
src/test/java/com/jongwon/monad/
├── board/
│   ├── CreateBoard/
│   │   └── CreateBoardUseCaseTest.java
│   ├── GetBoard/
│   │   └── GetBoardUseCaseTest.java
│   ├── UpdateBoard/
│   │   └── UpdateBoardUseCaseTest.java
│   ├── DeleteBoard/
│   │   └── DeleteBoardUseCaseTest.java
│   ├── domain/
│   │   └── BoardTest.java               ← 도메인 규칙 테스트
│   └── fake/
│       └── FakeBoardRepository.java
│
├── post/
│   ├── domain/
│   │   └── PostTest.java
│   ├── fake/
│   │   └── FakePostRepository.java
│   ├── CreatePost/
│   │   └── CreatePostUseCaseTest.java
│   └── ...
│
└── fixture/
    ├── BoardFixture.java
    └── PostFixture.java
```

### 7.6 필수 테스트 목록

**Board 도메인:**
- Board 생성 시 name 빈 문자열이면 예외
- Board 생성 시 name 50자 초과면 예외
- Board 정상 생성
- Board 수정

**Board UseCase:**
- 게시판 생성 성공
- 중복 이름으로 생성 시 예외
- 존재하지 않는 게시판 조회 시 예외
- 게시판 수정 성공
- 게시판 삭제 성공

**Post 도메인:**
- Post 생성 시 title 빈 문자열이면 예외
- Post 생성 시 content 빈 문자열이면 예외
- Post 정상 생성
- 조회수 증가
- Post 수정

**Post UseCase:**
- 게시글 작성 성공
- 존재하지 않는 게시판에 글 작성 시 예외
- 게시글 조회 시 조회수 증가
- 존재하지 않는 게시글 조회 시 예외
- 게시글 수정 / 삭제 성공

---

## 8. 예외 처리

`GlobalExceptionHandler`에서 공통 처리:

| 예외                     | HTTP Status | 응답 형태                            |
|------------------------|-------------|----------------------------------|
| EntityNotFoundException | 404         | `{ message: "..." }`            |
| IllegalArgumentException | 400       | `{ message: "..." }`            |
| MethodArgumentNotValid | 400         | `{ message: "...", errors: [] }` |
| 그 외 Exception          | 500         | `{ message: "서버 오류" }`           |

---

## 9. 구현 순서 (TDD 기반)

1. **사전 작업** — build.gradle.kts 의존성 추가 (Lombok)
2. **Board 도메인** — BoardTest 작성 → Board 엔티티 구현
3. **Board Port** — BoardRepository 인터페이스 정의
4. **Board Fake + Fixture** — FakeBoardRepository, BoardFixture 작성
5. **Board UseCase** — 각 슬라이스별 UseCase 테스트 작성 → UseCase 구현
6. **Board Controller** — 각 슬라이스별 Controller 작성
7. **Post 도메인** — PostTest 작성 → Post 엔티티 구현
8. **Post Port** — PostRepository 인터페이스 정의
9. **Post Fake + Fixture** — FakePostRepository, PostFixture 작성
10. **Post UseCase** — 각 슬라이스별 UseCase 테스트 작성 → UseCase 구현
11. **Post Controller** — 각 슬라이스별 Controller 작성
12. **Global** — GlobalExceptionHandler 구현
13. **전체 빌드 확인** — ./gradlew build 통과 확인

---

## 10. 이번 범위에서 제외하는 항목

- 회원(Member) 도메인 — 추후 구현
- 댓글(Comment) 도메인 — 추후 구현
- 인증/인가 (Spring Security, JWT 등)
- 실제 DB Adapter 구현 (DB 선택 후)
- 파일 업로드
- 좋아요 기능
- 검색 기능
