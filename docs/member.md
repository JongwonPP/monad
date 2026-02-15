# 회원(Member) 도메인 개발 계획

## 1. 목표

커뮤니티 게시판의 회원 관리 기능을 구현한다.
회원가입, 프로필 조회/수정, 탈퇴 등 기본 CRUD를 제공한다.

인증/인가(로그인, JWT, Spring Security)는 이번 범위에 포함하지 않는다.
비밀번호는 평문 저장하며, 인증 구현 시 해싱(BCrypt)으로 전환한다.

### 설계 원칙

기존 Board/Post 도메인과 동일:
- Vertical Slice Architecture
- DDD — 순수 Java 도메인 엔티티, Port 인터페이스
- TDD — 도메인 규칙 테스트 먼저, UseCase 테스트 먼저
- DB 비의존 — InMemory Adapter + Fake

---

## 2. 도메인 모델

### 2.1 Member (Aggregate Root)

| 필드        | 타입            | 제약 조건             | 설명                    |
|-----------|---------------|-------------------|-----------------------|
| id        | Long          | PK                | 식별자                   |
| email     | String        | NOT NULL, UNIQUE  | 로그인용 이메일              |
| password  | String        | NOT NULL          | 비밀번호 (평문, 추후 해싱 전환)  |
| nickname  | String        | NOT NULL, UNIQUE  | 커뮤니티 활동명              |
| createdAt | LocalDateTime |                   | 가입 시각                 |
| updatedAt | LocalDateTime |                   | 수정 시각                 |

**도메인 규칙:**
- email: 빈 문자열 불가, 이메일 형식 검증 (@ 포함), 최대 100자
- password: 빈 문자열 불가, 최소 8자, 최대 100자
- nickname: 빈 문자열 불가, 최소 2자, 최대 20자
- email, nickname 중복 검증은 UseCase에서 Repository를 통해 수행

**메서드:**
- `Member.create(email, password, nickname)` — 정적 팩토리, 검증 포함
- `updateProfile(nickname)` — 닉네임 변경
- `changePassword(oldPassword, newPassword)` — 기존 비밀번호 확인 후 변경
- `assignId(Long id)` — Fake/Adapter용

---

## 3. API 명세

### `/api/v1/members`

| Method | Endpoint                  | 설명       | Request Body                         | Response       |
|--------|---------------------------|----------|--------------------------------------|----------------|
| POST   | `/api/v1/members`         | 회원가입     | `{ email, password, nickname }`      | 201 Created    |
| GET    | `/api/v1/members/{id}`    | 프로필 조회   | —                                    | 200 OK         |
| PUT    | `/api/v1/members/{id}`    | 프로필 수정   | `{ nickname }`                       | 200 OK         |
| PATCH  | `/api/v1/members/{id}/password` | 비밀번호 변경 | `{ oldPassword, newPassword }` | 200 OK         |
| DELETE | `/api/v1/members/{id}`    | 회원 탈퇴    | —                                    | 204 No Content |

---

## 4. 패키지 구조 (Vertical Slice)

```
com.jongwon.monad/
├── member/
│   ├── signup/
│   │   ├── SignUpRequest.java           (record)
│   │   ├── SignUpResponse.java          (record)
│   │   ├── SignUpUseCase.java
│   │   └── SignUpController.java
│   ├── getmember/
│   │   ├── GetMemberResponse.java
│   │   ├── GetMemberUseCase.java
│   │   └── GetMemberController.java
│   ├── updatemember/
│   │   ├── UpdateMemberRequest.java
│   │   ├── UpdateMemberResponse.java
│   │   ├── UpdateMemberUseCase.java
│   │   └── UpdateMemberController.java
│   ├── changepassword/
│   │   ├── ChangePasswordRequest.java
│   │   ├── ChangePasswordUseCase.java
│   │   └── ChangePasswordController.java
│   ├── deletemember/
│   │   ├── DeleteMemberUseCase.java
│   │   └── DeleteMemberController.java
│   ├── domain/
│   │   ├── Member.java                  (Aggregate Root, 순수 Java)
│   │   └── MemberRepository.java        (Port 인터페이스)
│   └── infra/
│       └── InMemoryMemberRepository.java
```

---

## 5. Port 인터페이스

```java
public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    void deleteById(Long id);
}
```

---

## 6. 테스트 전략

### 6.1 테스트 디렉토리 구조

```
src/test/java/com/jongwon/monad/
├── member/
│   ├── domain/
│   │   └── MemberTest.java
│   ├── signup/
│   │   └── SignUpUseCaseTest.java
│   ├── getmember/
│   │   └── GetMemberUseCaseTest.java
│   ├── updatemember/
│   │   └── UpdateMemberUseCaseTest.java
│   ├── changepassword/
│   │   └── ChangePasswordUseCaseTest.java
│   ├── deletemember/
│   │   └── DeleteMemberUseCaseTest.java
│   └── fake/
│       └── FakeMemberRepository.java
│
└── fixture/
    └── MemberFixture.java
```

### 6.2 필수 테스트 목록

**Member 도메인:**
- 정상 생성
- email 빈 문자열 / null → 예외
- email 형식 불일치 (@ 미포함) → 예외
- email 100자 초과 → 예외
- password 빈 문자열 / null → 예외
- password 8자 미만 → 예외
- nickname 빈 문자열 / null → 예외
- nickname 2자 미만 / 20자 초과 → 예외
- 프로필 수정 (닉네임 변경)
- 비밀번호 변경 성공
- 비밀번호 변경 시 기존 비밀번호 불일치 → 예외

**UseCase:**
- 회원가입 성공
- 이메일 중복 → 예외
- 닉네임 중복 → 예외
- 프로필 조회 성공 / 존재하지 않는 회원 → 예외
- 프로필 수정 성공
- 비밀번호 변경 성공 / 기존 비밀번호 불일치 → 예외
- 회원 탈퇴 성공 / 존재하지 않는 회원 → 예외

---

## 7. 구현 순서 (TDD 기반)

1. **Member 도메인** — MemberTest 작성 → Member 엔티티 구현
2. **Member Port** — MemberRepository 인터페이스 정의
3. **Member Fake + Fixture** — FakeMemberRepository, MemberFixture 작성
4. **Member InMemory Adapter** — InMemoryMemberRepository 구현
5. **Vertical Slices** — 각 슬라이스별 UseCase 테스트 → UseCase → Controller
   - SignUp → GetMember → UpdateMember → ChangePassword → DeleteMember
6. **전체 빌드 확인** — ./gradlew build 통과

---

## 8. Post 연동 (추후)

현재 Post.author는 String이다. Member 도메인 완성 후 다음 단계에서:
- Post.author(String) → Post.memberId(Long)로 전환
- CreatePost에서 Member 존재 여부 확인
- 게시글 조회 시 작성자 닉네임 반환

이번 범위에서는 Member CRUD만 구현하고, Post 연동은 별도 작업으로 분리한다.

---

## 9. 이번 범위에서 제외하는 항목

- 인증/인가 (로그인, JWT, Spring Security)
- 비밀번호 해싱 (BCrypt) — 인증 구현 시 전환
- Post.author → memberId 연동
- 프로필 이미지
- 회원 목록 조회 (관리자 기능)
