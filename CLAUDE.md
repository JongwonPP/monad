# CLAUDE.md

## Project Overview

커뮤니티 게시판 서비스. 사용자가 가입하고, 글을 쓰고, 댓글을 달며 소통하는 기본적인 게시판을 구현한다.

핵심 도메인:
- **Member** — 회원 (가입, 로그인, 프로필)
- **Board** — 게시판 (카테고리 분류)
- **Post** — 게시글 (CRUD, 조회수, 좋아요)
- **Comment** — 댓글 (게시글에 대한 댓글, 대댓글)

## Tech Stack

- Java 25
- Spring Boot 4.0.2
- Gradle (Kotlin DSL) — wrapper 9.3.1
- Spring Web (REST API)
- DB/JPA — 미설정 (추가 예정)

## Project Structure

```
src/main/java/com/jongwon/monad/
├── MonadApplication.java          # Entry point
├── domain/                        # 도메인별 패키지 (예정)
│   ├── member/
│   ├── board/
│   ├── post/
│   └── comment/
└── global/                        # 공통 설정, 예외 처리 (예정)

src/main/resources/
└── application.yml                # port: 8080

src/test/java/com/jongwon/monad/
└── MonadApplicationTests.java
```

각 도메인 패키지 하위 구조:
```
domain/{name}/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

## Build & Run

```bash
./gradlew build
./gradlew bootRun    # http://localhost:8080
./gradlew test
```

## Code Conventions

- 패키지: 도메인별 분리 (`domain.post.controller`, `domain.post.service` 등)
- REST API 경로: `/api/v1/{도메인}` (예: `/api/v1/posts`)
- 엔티티에 Lombok 사용 시 `@Getter`, `@NoArgsConstructor(access = PROTECTED)` 기본 적용
- Request/Response DTO는 record 사용 권장
- 서비스 계층에 `@Transactional(readOnly = true)` 기본, 변경 메서드만 `@Transactional`

## Important Notes

- 현재 브랜치: `feature/standard-board-crud` — 게시판 CRUD 구현 진행 중
- DB, JPA 의존성 아직 미추가 — 기능 구현 시 build.gradle.kts에 추가 필요
- 프로젝트는 초기 상태이며, MonadApplication.java 외 소스 코드 없음
