# 인증/인가(Auth) 구현 계획

## 1. 목표

JWT 기반 인증/인가를 구현하여 API 접근을 제어한다.
- 로그인 시 JWT 토큰 발급 (Access Token + Refresh Token)
- 보호된 API는 토큰 없이 접근 불가
- 비밀번호는 BCrypt로 해싱 저장
- 기존 author(String)를 인증된 사용자 정보로 대체

### 설계 원칙

- 기존 Port/Adapter 패턴 유지 — 비밀번호 해싱도 Port로 추상화
- Spring Security 필터 체인으로 인증 처리
- 도메인 로직에 Spring Security 의존 최소화

---

## 2. 의존성 추가

```kotlin
implementation("org.springframework.boot:spring-boot-starter-security")
implementation("io.jsonwebtoken:jjwt-api:0.12.6")
runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

testImplementation("org.springframework.security:spring-security-test")
```

---

## 3. 인증 흐름

### 3.1 로그인

```
POST /api/v1/auth/login { email, password }
    ↓
AuthUseCase → MemberRepository.findByEmail()
    ↓
PasswordEncoder.matches(rawPassword, hashedPassword) 확인
    ↓
JwtTokenProvider.generateAccessToken(memberId, email, nickname)
JwtTokenProvider.generateRefreshToken(memberId)
    ↓
Response: { accessToken, refreshToken }
```

### 3.2 토큰 갱신

```
POST /api/v1/auth/refresh { refreshToken }
    ↓
JwtTokenProvider.validateToken(refreshToken)
    ↓
새 accessToken 발급
    ↓
Response: { accessToken }
```

### 3.3 인증된 요청

```
GET /api/v1/posts (Authorization: Bearer {accessToken})
    ↓
JwtAuthenticationFilter → 토큰 검증 → SecurityContext에 인증 정보 설정
    ↓
Controller → UseCase 실행
```

---

## 4. 토큰 설계

### 4.1 Access Token

| 항목     | 값                |
|--------|------------------|
| 타입     | JWT (Bearer)     |
| 만료     | 30분              |
| Payload | memberId, email, nickname |
| 용도     | API 인증           |

### 4.2 Refresh Token

| 항목   | 값              |
|------|----------------|
| 타입   | JWT            |
| 만료   | 7일             |
| Payload | memberId      |
| 용도   | Access Token 재발급 |

### 4.3 application.yml 설정

```yaml
jwt:
  secret: (Base64 인코딩된 256bit 이상 시크릿 키)
  access-token-expiry: 1800000    # 30분 (ms)
  refresh-token-expiry: 604800000 # 7일 (ms)
```

---

## 5. API 접근 제어

### 5.1 공개 API (인증 불필요)

| Endpoint | 설명 |
|----------|------|
| POST /api/v1/members | 회원가입 |
| POST /api/v1/auth/login | 로그인 |
| POST /api/v1/auth/refresh | 토큰 갱신 |
| GET /api/v1/boards | 게시판 목록 |
| GET /api/v1/boards/{id} | 게시판 조회 |
| GET /api/v1/boards/{boardId}/posts | 게시글 목록 |
| GET /api/v1/boards/{boardId}/posts/{postId} | 게시글 조회 |
| GET /api/v1/posts/{postId}/comments | 댓글 목록 |

### 5.2 인증 필요 API

| Endpoint | 권한 규칙 |
|----------|----------|
| POST /api/v1/boards | 인증된 사용자 |
| PUT/DELETE /api/v1/boards/{id} | 인증된 사용자 (추후 관리자 권한) |
| POST /api/v1/boards/{boardId}/posts | 인증된 사용자 |
| PUT /api/v1/boards/{boardId}/posts/{postId} | 본인 글만 수정 |
| DELETE /api/v1/boards/{boardId}/posts/{postId} | 본인 글만 삭제 |
| POST /api/v1/posts/{postId}/comments | 인증된 사용자 |
| POST /api/v1/posts/{postId}/comments/{id}/replies | 인증된 사용자 |
| PUT /api/v1/posts/{postId}/comments/{id} | 본인 댓글만 수정 |
| DELETE /api/v1/posts/{postId}/comments/{id} | 본인 댓글만 삭제 |
| GET/PUT/DELETE /api/v1/members/{id} | 본인만 |
| PATCH /api/v1/members/{id}/password | 본인만 |

---

## 6. 패키지 구조

```
com.jongwon.monad/
├── auth/
│   ├── login/
│   │   ├── LoginRequest.java           (record)
│   │   ├── LoginResponse.java          (record)
│   │   ├── LoginUseCase.java
│   │   └── LoginController.java
│   ├── refresh/
│   │   ├── RefreshRequest.java
│   │   ├── RefreshResponse.java
│   │   ├── RefreshUseCase.java
│   │   └── RefreshController.java
│   └── domain/
│       ├── PasswordEncoder.java        (Port 인터페이스)
│       └── TokenProvider.java          (Port 인터페이스)
│
├── global/
│   ├── exception/                      (기존)
│   └── security/
│       ├── SecurityConfig.java         (필터 체인, 공개/보호 경로 설정)
│       ├── JwtAuthenticationFilter.java (OncePerRequestFilter)
│       ├── JwtTokenProvider.java       (TokenProvider 구현체)
│       ├── BcryptPasswordEncoder.java  (PasswordEncoder 구현체)
│       ├── AuthenticationPrincipal.java (인증된 사용자 정보 record)
│       └── AuthAccessDeniedHandler.java (403 응답 처리)
```

---

## 7. Port 인터페이스

### 7.1 PasswordEncoder (auth/domain/)

```java
public interface PasswordEncoder {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
```

- 구현체: BcryptPasswordEncoder (global/security/)
- 테스트용: FakePasswordEncoder (평문 비교)

### 7.2 TokenProvider (auth/domain/)

```java
public interface TokenProvider {
    String generateAccessToken(Long memberId, String email, String nickname);
    String generateRefreshToken(Long memberId);
    Long getMemberIdFromToken(String token);
    boolean validateToken(String token);
}
```

- 구현체: JwtTokenProvider (global/security/)
- 테스트용: FakeTokenProvider (간단한 문자열 토큰)

---

## 8. 기존 코드 변경사항

### 8.1 Member 도메인

- `Member.create()` — password를 해싱된 값으로 저장 (UseCase에서 PasswordEncoder 사용)
- `Member.changePassword()` — PasswordEncoder.matches()로 비교 변경
  - 현재: `this.password.equals(oldPassword)` (평문 비교)
  - 변경: UseCase에서 PasswordEncoder.matches() 호출 후 Member에 새 해싱 비밀번호 설정

### 8.2 Post 도메인

- `Post.author(String)` → `Post.memberId(Long)` 전환
- CreatePost: author 파라미터 대신 인증된 사용자의 memberId 사용
- Post 조회 응답에 작성자 닉네임 포함 (MemberRepository로 조회)

### 8.3 Comment 도메인

- `Comment.author(String)` → `Comment.memberId(Long)` 전환
- CreateComment/CreateReply: 인증된 사용자의 memberId 사용
- Comment 조회 응답에 작성자 닉네임 포함

### 8.4 기존 UseCase 변경

- 쓰기 작업(Create/Update/Delete) UseCase에 memberId 파라미터 추가
- Update/Delete UseCase에서 본인 확인 로직 추가
- Controller에서 SecurityContext → memberId 추출하여 UseCase에 전달

---

## 9. 테스트 전략

### 9.1 테스트 디렉토리

```
src/test/java/com/jongwon/monad/
├── auth/
│   ├── login/
│   │   └── LoginUseCaseTest.java
│   ├── refresh/
│   │   └── RefreshUseCaseTest.java
│   └── fake/
│       ├── FakePasswordEncoder.java
│       └── FakeTokenProvider.java
```

### 9.2 FakePasswordEncoder

```java
public class FakePasswordEncoder implements PasswordEncoder {
    public String encode(String rawPassword) {
        return "encoded_" + rawPassword;  // 단순 prefix
    }
    public boolean matches(String rawPassword, String encodedPassword) {
        return encodedPassword.equals("encoded_" + rawPassword);
    }
}
```

### 9.3 FakeTokenProvider

```java
public class FakeTokenProvider implements TokenProvider {
    public String generateAccessToken(Long memberId, String email, String nickname) {
        return "access_" + memberId;
    }
    public String generateRefreshToken(Long memberId) {
        return "refresh_" + memberId;
    }
    public Long getMemberIdFromToken(String token) {
        return Long.valueOf(token.split("_")[1]);
    }
    public boolean validateToken(String token) {
        return token != null && token.contains("_");
    }
}
```

### 9.4 필수 테스트 목록

**Login UseCase:**
- 로그인 성공 → accessToken + refreshToken 반환
- 존재하지 않는 이메일 → 예외
- 비밀번호 불일치 → 예외

**Refresh UseCase:**
- 토큰 갱신 성공 → 새 accessToken 반환
- 유효하지 않은 refreshToken → 예외

**기존 UseCase 변경 테스트:**
- Post 작성 시 memberId로 저장 확인
- Post 수정/삭제 시 본인 확인
- Comment 작성 시 memberId로 저장 확인
- Comment 수정/삭제 시 본인 확인

---

## 10. 구현 순서

1. **의존성 추가** — build.gradle.kts에 Spring Security, jjwt 추가
2. **Port 인터페이스** — PasswordEncoder, TokenProvider 정의
3. **Fake 구현** — FakePasswordEncoder, FakeTokenProvider
4. **Auth 도메인** — Login UseCase 테스트 → 구현, Refresh UseCase 테스트 → 구현
5. **Security 인프라** — JwtTokenProvider, BcryptPasswordEncoder, JwtAuthenticationFilter, SecurityConfig
6. **Member 변경** — 비밀번호 해싱 적용 (SignUp, ChangePassword)
7. **Post 변경** — author → memberId 전환, 본인 확인 로직
8. **Comment 변경** — author → memberId 전환, 본인 확인 로직
9. **GlobalExceptionHandler 업데이트** — 401/403 응답 추가
10. **전체 빌드 확인** — ./gradlew build 통과

---

## 11. 이번 범위에서 제외하는 항목

- 역할(Role) 기반 권한 관리 (ADMIN, USER 등)
- OAuth2 소셜 로그인
- Refresh Token 저장소 (Redis 등) — 현재는 토큰 자체 검증만
- 로그아웃 (토큰 블랙리스트)
- Rate Limiting
- CORS 세부 설정
