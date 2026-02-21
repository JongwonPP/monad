# API 문서 자동화 (SpringDoc OpenAPI)

## 개요

수동으로 관리하던 API 명세(api-spec.md, api-spec-2.md, api-spec-3.md)를 코드 기반으로 자동 생성한다.
SpringDoc OpenAPI를 도입하여 Swagger UI를 통해 API를 브라우저에서 바로 테스트할 수 있게 한다.

---

## 1. 의존성

```kotlin
// build.gradle.kts
implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
implementation("org.springframework.boot:spring-boot-jackson2") // Spring Boot 4.0 + Jackson 2 호환
```

- `springdoc-openapi-starter-webmvc-ui:3.0.1` — Spring Boot 4.0 지원
- `spring-boot-jackson2` — Spring Boot 4.0이 Jackson 3을 기본으로 사용하나 swagger-core가 Jackson 2에 의존하므로 호환 모듈 필요

---

## 2. 설정

### application.yml

```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: method
```

### SecurityConfig — Swagger UI 경로 permitAll

```java
.requestMatchers("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**").permitAll()
```

### OpenAPI 전역 설정 — SwaggerConfig.java

`global/config/SwaggerConfig.java`에 OpenAPI 메타데이터 + JWT 인증 스키마 설정:

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Monad 커뮤니티 게시판 API")
                        .description("회원, 게시판, 게시글, 댓글, 이미지 첨부 API")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Token",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
```

---

## 3. 컨트롤러 어노테이션

각 컨트롤러에 `@Tag`와 `@Operation`을 추가하여 그룹화 및 설명을 제공한다.

### 태그 그룹

| @Tag name | 대상 컨트롤러 |
|-----------|-------------|
| Auth | LoginController, RefreshController |
| Member | SignUpController, GetMemberController, UpdateMemberController, ChangePasswordController, DeleteMemberController |
| Board | CreateBoardController, GetBoardController, ListBoardsController, UpdateBoardController, DeleteBoardController |
| Post | CreatePostController, GetPostController, ListPostsController, UpdatePostController, DeletePostController, SearchPostsController, MyPostsController |
| Post Like | LikePostController, UnlikePostController |
| Comment | CreateCommentController, CreateReplyController, ListCommentsController, UpdateCommentController, DeleteCommentController, MyCommentsController |
| Comment Like | LikeCommentController, UnlikeCommentController |
| Image | UploadImageController, GetImageController, DeleteImageController |

### 어노테이션 예시

```java
@Tag(name = "Post", description = "게시글 API")
@RestController
public class CreatePostController {

    @Operation(summary = "게시글 작성", description = "게시판에 새 게시글을 작성한다")
    @PostMapping("/api/v1/boards/{boardId}/posts")
    public ResponseEntity<CreatePostResponse> createPost(...) { ... }
}
```

### 에러 응답 문서화

GlobalExceptionHandler의 에러 응답 형식을 `@ApiResponse`로 문서화:

```java
@Operation(
    summary = "게시글 작성",
    responses = {
        @ApiResponse(responseCode = "201", description = "작성 성공"),
        @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
        @ApiResponse(responseCode = "404", description = "게시판 없음")
    }
)
```

---

## 4. 대상 컨트롤러 (33개)

| 도메인 | 컨트롤러 수 |
|--------|-----------|
| Auth | 2 |
| Member | 5 |
| Board | 5 |
| Post | 7 |
| Post Like | 2 |
| Comment | 6 |
| Comment Like | 2 |
| Image | 3 |
| Health | 1 |

---

## 5. 구현 순서

1. `build.gradle.kts`에 의존성 추가
2. `SwaggerConfig.java` 생성 (OpenAPI 메타 + JWT 스키마)
3. `application.yml`에 springdoc 설정 추가
4. `SecurityConfig`에 Swagger 경로 permitAll 추가
5. 전체 33개 컨트롤러에 `@Tag` + `@Operation` + `@ApiResponse` 어노테이션 추가
6. 빌드 확인 + Swagger UI 접속 테스트 (`http://localhost:8080/swagger-ui.html`)
