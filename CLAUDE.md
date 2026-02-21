# CLAUDE.md

커뮤니티 게시판 서비스 — Member, Board, Post, Comment, Auth (모두 구현 완료)

## Tech Stack

Java 25 / Spring Boot 4.0.2 / Gradle (Kotlin DSL) / Spring Security + JWT (JJWT 0.12.6) / Spring JDBC + H2 + Flyway / Lombok

## Architecture

- **Vertical Slice** — UseCase 단위 패키지 (기술 계층 X)
- **DDD** — 순수 Java 도메인 엔티티 (프레임워크 무의존)
- **Port/Adapter** — Repository 인터페이스(Port) + Profile별 Adapter (`local`: Fake, `prod`: JDBC)
- **TDD** — Fake 객체 + TestFixture, DB 무의존 단위 테스트

## Code Conventions

- **도메인 엔티티**: 순수 Java, `create()` 생성 / `reconstruct()` DB 복원, 도메인 검증 내장
- **패키지**: Vertical Slice (lowercase: `board.createboard`, `member.signup`)
- **Port**: 도메인 패키지에 인터페이스, infra/security 패키지에 구현체
- **DTO**: record + jakarta.validation
- **UseCase**: @Service + constructor injection
- **Controller**: @RestController + constructor injection, ResponseEntity 반환
- **테스트**: Fake 객체(src/main/infra, @Profile 없이 직접 생성) + TestFixture(src/test/fixture)
- **도메인 간 참조**: ID만 사용 (객체 참조 없음)
- **인수 테스트는 무엇을(WHAT) 정의**하고, **단위 테스트는 어떻게(HOW)를 정의**한다.
- **구현 순서**: /docs 디렉토리 하위에 **구현방법에 대한 계획 문서** 작성, 확인받고 다음 문서를 토대로 구현

## Build & Run

```bash
./gradlew build
./gradlew bootRun                                          # local (기본, Fake/InMemory)
./gradlew bootRun --args='--spring.profiles.active=prod'   # prod (H2 + Flyway)
./gradlew test
```

## Detailed Docs

- `docs/architecture.md` — 프로젝트 구조, Vertical Slice 패턴, 테스트 구조
- `docs/api-spec.md` — 전체 API 명세 (Request/Response, 도메인 규칙)
- `docs/database.md` — Profile 구성, JDBC 패턴, Flyway 마이그레이션, H2 콘솔
