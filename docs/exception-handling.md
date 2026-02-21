# 예외 처리 정리

## 현황 분석

현재 예외 처리는 `IllegalArgumentException` 하나로 성격이 다른 에러를 모두 처리하고 있어 HTTP 상태 코드가 부정확하다.

### 문제점

| 문제 | 현재 | 올바른 상태 코드 | 영향 |
|------|------|-----------------|------|
| 권한 없음 ("본인의 글만 수정/삭제") | 400 | **403 Forbidden** | 6곳 |
| 중복 ("이미 사용 중인 이메일/닉네임") | 400 | **409 Conflict** | 3곳 |
| 로그인 실패 ("비밀번호 불일치") | 400 | **401 Unauthorized** | 2곳 |
| 인증 안 된 사용자가 보호 API 접근 | Spring 기본 HTML | **401 JSON** | SecurityConfig |
| 파일 I/O 실패 | RuntimeException → 500 | 적절한 커스텀 예외 | LocalImageStorage |

---

## 해결 방안

### 1. 커스텀 예외 클래스 추가

`global/exception/` 패키지에 추가:

```
global/exception/
├── EntityNotFoundException.java     (기존, 404)
├── AuthorizationException.java      (신규, 403)
├── DuplicateException.java          (신규, 409)
└── GlobalExceptionHandler.java      (수정)
```

| 예외 클래스 | HTTP 상태 | 용도 |
|------------|-----------|------|
| `EntityNotFoundException` | 404 | 엔티티 조회 실패 (기존) |
| `AuthorizationException` | 403 | 본인 리소스가 아닌 경우 |
| `DuplicateException` | 409 | 이메일/닉네임/게시판명 중복 |
| `IllegalArgumentException` | 400 | 입력값 검증 실패 (도메인 규칙, 비즈니스 로직) |

### 2. 로그인 실패 처리

로그인 실패는 Spring Security의 `AuthenticationException`을 그대로 활용한다.
- `LoginUseCase`에서 `IllegalArgumentException` 대신 `AuthenticationException` 하위 클래스 사용
- `BadCredentialsException` (Spring Security 제공) — 이메일 불일치, 비밀번호 불일치
- GlobalExceptionHandler에 이미 `AuthenticationException` → 401 핸들러 존재

### 3. AuthenticationEntryPoint 추가

인증 안 된 사용자가 보호 API 접근 시 JSON 401 응답 반환.

```java
// global/security/AuthEntryPoint.java
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(...) {
        response.setStatus(401);
        response.getWriter().write("{\"message\":\"인증이 필요합니다\"}");
    }
}
```

SecurityConfig에 `.authenticationEntryPoint(authEntryPoint)` 추가.

### 4. LocalImageStorage 정리

`store()`, `delete()`의 `RuntimeException`을 제거하고 체크 예외를 언체크로 감싸는 대신, GlobalExceptionHandler의 `Exception` 핸들러(500)가 처리하도록 의미 있는 메시지를 가진 `RuntimeException` 유지. 단, 로깅 추가.

### 5. UploadImageController IOException 처리

`file.getBytes()` 호출의 `throws IOException` 제거하고, controller 내에서 try-catch 후 `IllegalArgumentException`으로 변환.

---

## 변경 대상 파일

### 신규 생성
- `global/exception/AuthorizationException.java`
- `global/exception/DuplicateException.java`
- `global/security/AuthEntryPoint.java`

### 수정 — GlobalExceptionHandler
- `AuthorizationException` → 403 핸들러 추가
- `DuplicateException` → 409 핸들러 추가

### 수정 — SecurityConfig
- `.authenticationEntryPoint(authEntryPoint)` 추가

### 수정 — UseCase (IllegalArgumentException → AuthorizationException)
- `post/updatepost/UpdatePostUseCase.java` — "본인의 글만 수정"
- `post/deletepost/DeletePostUseCase.java` — "본인의 글만 삭제"
- `post/uploadimage/UploadImageUseCase.java` — "본인의 게시글만 이미지 첨부"
- `post/deleteimage/DeleteImageUseCase.java` — "본인의 게시글 이미지만 삭제"
- `comment/updatecomment/UpdateCommentUseCase.java` — "본인의 댓글만 수정"
- `comment/deletecomment/DeleteCommentUseCase.java` — "본인의 댓글만 삭제"

### 수정 — UseCase (IllegalArgumentException → DuplicateException)
- `member/signup/SignUpUseCase.java` — "이미 사용 중인 이메일/닉네임"
- `board/createboard/CreateBoardUseCase.java` — "이미 존재하는 게시판 이름"

### 수정 — UseCase (IllegalArgumentException → BadCredentialsException)
- `auth/login/LoginUseCase.java` — "존재하지 않는 이메일", "비밀번호 불일치"

### 수정 — Controller
- `post/uploadimage/UploadImageController.java` — IOException try-catch 처리

### 수정 — 테스트
- 위 UseCase 변경에 따라 테스트의 예외 타입 assertion 수정

---

## 변경 후 예외 매핑 정리

| HTTP | 예외 | 용도 |
|------|------|------|
| 400 | `IllegalArgumentException` | 도메인 검증 실패, 비즈니스 규칙 위반 |
| 400 | `MethodArgumentNotValidException` | Bean Validation 실패 |
| 401 | `AuthenticationException` | 인증 실패 (로그인 실패, 토큰 만료) |
| 403 | `AuthorizationException` | 권한 없음 (본인 리소스가 아님) |
| 404 | `EntityNotFoundException` | 리소스 없음 |
| 409 | `DuplicateException` | 중복 (이메일, 닉네임, 게시판명) |
| 500 | `Exception` | 서버 오류 |

---

## 구현 순서

1. 커스텀 예외 클래스 생성 (`AuthorizationException`, `DuplicateException`)
2. `AuthEntryPoint` 생성
3. `GlobalExceptionHandler` 수정 (403, 409 핸들러 추가)
4. `SecurityConfig` 수정 (entryPoint 추가)
5. UseCase 예외 타입 변경 (Authorization 6곳, Duplicate 3곳, Login 2곳)
6. `UploadImageController` IOException 처리
7. 테스트 수정
