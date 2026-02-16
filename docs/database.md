# Database 연동

## 개요

H2 + JdbcTemplate + Flyway 기반 DB 연동. Spring Profile로 local/prod 환경을 분리하여, local에서는 InMemory 어댑터로 DB 없이 빠르게 개발하고, prod에서는 실제 DB를 사용한다.

## Profile 구성

| Profile | Repository 구현체 | DB | 설명 |
|---------|------------------|-----|------|
| `local` (기본) | `Fake*Repository` | 없음 | ConcurrentHashMap 기반, 앱 재시작 시 데이터 초기화 |
| `prod` | `Jdbc*Repository` | H2 In-Memory | JdbcTemplate + Flyway 마이그레이션 |

### 실행 방법

```bash
# local (기본값)
./gradlew bootRun

# prod
./gradlew bootRun --args='--spring.profiles.active=prod'

# 환경변수로 지정
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun
```

## 설정 파일

### application.yml (공통)
- 서버 포트, JWT 설정 등 프로필에 무관한 공통 설정
- `spring.profiles.active: local` — 기본 프로필 지정

### application-local.yml
- `DataSourceAutoConfiguration`, `FlywayAutoConfiguration` 자동설정 제외
- DB 관련 빈이 생성되지 않으므로 InMemory 어댑터만 동작

### application-prod.yml
- H2 In-Memory datasource (`jdbc:h2:mem:monad;DB_CLOSE_DELAY=-1`)
- Flyway 마이그레이션 활성화 (`classpath:db/migration`)
- H2 콘솔 활성화 (`/h2-console`)

## Port/Adapter 구조

```
domain/
└── MemberRepository.java          ← Port (인터페이스)

infra/
├── FakeMemberRepository.java      ← @Profile("local")
└── JdbcMemberRepository.java      ← @Profile("prod")
```

- Port(인터페이스)는 도메인 패키지에 위치
- Adapter(구현체)는 infra 패키지에 위치
- `@Profile` 어노테이션으로 프로필별 빈 등록
- UseCase는 Port에만 의존하므로 어댑터 교체에 영향 없음

### 어댑터 목록

| Port | local 어댑터 | prod 어댑터 |
|------|-------------|------------|
| `MemberRepository` | `FakeMemberRepository` | `JdbcMemberRepository` |
| `BoardRepository` | `FakeBoardRepository` | `JdbcBoardRepository` |
| `PostRepository` | `FakePostRepository` | `JdbcPostRepository` |
| `CommentRepository` | `FakeCommentRepository` | `JdbcCommentRepository` |

## 도메인 엔티티 — reconstruct()

DB에서 로드한 데이터로 도메인 객체를 복원하기 위한 정적 팩토리 메서드.

| 메서드 | 용도 | 검증 |
|--------|------|------|
| `create()` | 새 엔티티 생성 | 도메인 검증 수행 |
| `reconstruct()` | DB 데이터 복원 | 검증 생략 (이미 검증된 데이터) |

```java
// 새로 생성 — 검증 수행
Member member = Member.create(email, password, nickname);

// DB에서 복원 — 검증 생략, id/timestamps 직접 주입
Member member = Member.reconstruct(id, email, password, nickname, createdAt, updatedAt);
```

## Flyway 마이그레이션

### 파일 위치
`src/main/resources/db/migration/`

### 현재 버전

#### V1__init_schema.sql

테이블 4개 + 인덱스 4개 생성:

```sql
-- 테이블
member    (id, email, password, nickname, created_at, updated_at)
board     (id, name, description, created_at, updated_at)
post      (id, board_id, title, content, member_id, view_count, created_at, updated_at)
comment   (id, post_id, parent_id, member_id, content, created_at, updated_at)

-- 인덱스
idx_post_board_id      ON post(board_id)
idx_post_created_at    ON post(created_at)
idx_comment_post_id    ON comment(post_id)
idx_comment_parent_id  ON comment(parent_id)
```

### FK 제약 조건

현재 미포함. 이유:
- `DeleteBoardUseCase`, `DeleteMemberUseCase`에 cascade 삭제 로직이 없음
- FK가 있으면 부모 삭제 시 DB 레벨에서 오류 발생
- 추후 cascade 로직 구현 후 별도 마이그레이션(`V2__add_foreign_keys.sql`)으로 추가 예정

## JDBC 어댑터 구현 패턴

### save() — INSERT/UPDATE 분기

```java
if (entity.getId() == null) {
    // INSERT + GeneratedKeyHolder → assignId()
} else {
    // UPDATE (변경 가능한 필드만)
}
```

### UPDATE 대상 필드

| 엔티티 | UPDATE 대상 | 불변 필드 |
|--------|------------|----------|
| Member | email, password, nickname, updated_at | id, created_at |
| Board | name, description, updated_at | id, created_at |
| Post | title, content, view_count, updated_at | id, board_id, member_id, created_at |
| Comment | content, updated_at | id, post_id, parent_id, member_id, created_at |

### nullable 컬럼 처리 (Comment.parentId)

```java
// INSERT 시
if (comment.getParentId() != null) {
    ps.setLong(2, comment.getParentId());
} else {
    ps.setNull(2, Types.BIGINT);
}

// SELECT 시
long parentIdValue = rs.getLong("parent_id");
Long parentId = rs.wasNull() ? null : parentIdValue;
```

## H2 Console

prod 프로필에서만 사용 가능.

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:monad`
- Username: `sa`
- Password: (빈 값)

SecurityConfig에서 `/h2-console/**` 경로 permitAll + frameOptions 비활성화 설정 포함.

## 테스트

테스트는 `src/test`의 Fake 객체를 사용하므로 DB 연동과 무관하게 동작한다.

```bash
./gradlew test    # 모든 테스트 통과 (프로필/DB 무관)
```
