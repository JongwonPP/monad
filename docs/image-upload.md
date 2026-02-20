# 이미지 첨부 기능

## 개요

게시글에 이미지를 첨부하는 기능. 로컬 파일 시스템에 저장하며, 게시글 당 최대 5장.

---

## 1. API 설계

### 1.1 이미지 업로드

```
POST /api/v1/posts/{postId}/images
```

**인증**: 필요 (본인 글만)

**Content-Type**: `multipart/form-data`

**Path Parameters**:

| 파라미터 | 타입 | 설명 |
|---------|------|------|
| postId | number | 게시글 ID |

**Request Body** (multipart):

| 필드 | 타입 | 필수 | 제약 조건 |
|------|------|------|----------|
| file | file | O | JPEG/PNG/GIF/WEBP, 최대 5MB |

**Response** `201 Created`:
```json
{
  "id": 1,
  "postId": 1,
  "originalFilename": "photo.jpg",
  "storedFilename": "550e8400-e29b-41d4-a716-446655440000.jpg",
  "imageUrl": "/api/v1/images/550e8400-e29b-41d4-a716-446655440000.jpg",
  "fileSize": 204800,
  "contentType": "image/jpeg",
  "createdAt": "2025-01-15T10:30:00"
}
```

**에러**:
- `400` — 파일 형식 불가, 파일 크기 초과, 이미지 5장 초과, 본인 글이 아님
- `404` — 게시글 없음

---

### 1.2 이미지 조회 (서빙)

```
GET /api/v1/images/{storedFilename}
```

**인증**: 불필요

파일을 바이너리로 응답한다 (Content-Type: image/jpeg 등).

**Response** `200 OK`: 이미지 바이너리

**에러**:
- `404` — 이미지 없음

---

### 1.3 이미지 삭제

```
DELETE /api/v1/posts/{postId}/images/{imageId}
```

**인증**: 필요 (본인 글만)

**Response** `204 No Content`

**에러**:
- `400` — 본인의 글이 아님
- `404` — 게시글 또는 이미지 없음

---

### 1.4 기존 API 변경 — 게시글 조회

`GET /api/v1/boards/{boardId}/posts/{postId}` 응답에 `images` 필드 추가:

```json
{
  "id": 1,
  "boardId": 1,
  "title": "게시글 제목",
  "content": "게시글 본문",
  "memberId": 1,
  "nickname": "홍길동",
  "viewCount": 43,
  "likeCount": 5,
  "liked": true,
  "images": [
    {
      "id": 1,
      "imageUrl": "/api/v1/images/550e8400-e29b-41d4-a716-446655440000.jpg",
      "originalFilename": "photo.jpg"
    }
  ],
  "createdAt": "2025-01-15T10:30:00"
}
```

---

## 2. 도메인 모델

### PostImage 엔티티

```java
public class PostImage {
    private Long id;
    private Long postId;           // 불변
    private String originalFilename; // 불변, 원본 파일명
    private String storedFilename;  // 불변, UUID 기반 저장 파일명
    private String contentType;     // 불변, image/jpeg 등
    private long fileSize;          // 불변, 바이트 단위
    private LocalDateTime createdAt; // 불변
}
```

**도메인 규칙**:
- 허용 Content-Type: `image/jpeg`, `image/png`, `image/gif`, `image/webp`
- 최대 파일 크기: 5MB (5 * 1024 * 1024 bytes)
- 게시글 당 최대 이미지 수: 5장
- `storedFilename`: UUID + 확장자 (예: `550e8400-...-.jpg`)

**팩토리 메서드**:
- `create(postId, originalFilename, storedFilename, contentType, fileSize)` — 도메인 검증 수행
- `reconstruct(id, postId, originalFilename, storedFilename, contentType, fileSize, createdAt)` — DB 복원

---

## 3. Port/Adapter

### Port (인터페이스)

**PostImageRepository** — `post/domain/` 패키지:
```java
public interface PostImageRepository {
    PostImage save(PostImage postImage);
    Optional<PostImage> findById(Long id);
    Optional<PostImage> findByStoredFilename(String storedFilename);
    List<PostImage> findAllByPostId(Long postId);
    int countByPostId(Long postId);
    void deleteById(Long id);
    void deleteAllByPostId(Long postId);
}
```

**ImageStorage** — `post/domain/` 패키지 (파일 시스템 추상화):
```java
public interface ImageStorage {
    void store(String storedFilename, byte[] data);
    byte[] load(String storedFilename);
    void delete(String storedFilename);
}
```

### Adapter (구현체)

| Port | local 어댑터 | prod 어댑터 |
|------|-------------|------------|
| `PostImageRepository` | `FakePostImageRepository` | `JdbcPostImageRepository` |
| `ImageStorage` | `FakeImageStorage` (InMemory Map) | `LocalImageStorage` (파일 시스템) |

**LocalImageStorage**: `./data/images/` 디렉토리에 파일 저장.

---

## 4. DB 스키마

### V3__add_post_image_table.sql

```sql
CREATE TABLE post_image (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id           BIGINT       NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename   VARCHAR(255) NOT NULL,
    content_type      VARCHAR(50)  NOT NULL,
    file_size         BIGINT       NOT NULL,
    created_at        TIMESTAMP    NOT NULL
);

CREATE INDEX idx_post_image_post_id ON post_image(post_id);
CREATE UNIQUE INDEX idx_post_image_stored_filename ON post_image(stored_filename);
```

---

## 5. 패키지 구조

```
post/
├── domain/
│   ├── PostImage.java              (도메인 엔티티)
│   ├── PostImageRepository.java    (Port)
│   └── ImageStorage.java           (Port)
├── infra/
│   ├── FakePostImageRepository.java   (@Profile local)
│   ├── JdbcPostImageRepository.java   (@Profile prod)
│   ├── FakeImageStorage.java          (@Profile local)
│   └── LocalImageStorage.java         (@Profile prod)
├── uploadimage/
│   ├── UploadImageRequest.java     (validation 불필요 — MultipartFile 사용)
│   ├── UploadImageResponse.java    (record)
│   ├── UploadImageUseCase.java     (@Service)
│   └── UploadImageController.java  (@RestController)
├── getimage/
│   ├── GetImageUseCase.java        (@Service)
│   └── GetImageController.java     (@RestController)
└── deleteimage/
    ├── DeleteImageUseCase.java     (@Service)
    └── DeleteImageController.java  (@RestController)
```

---

## 6. Multipart 데이터 변환 원칙

`MultipartFile`은 Spring 프레임워크 의존 객체이므로, UseCase(도메인 계층)에 직접 전달하지 않는다.
Controller에서 도메인 객체로 변환한 뒤 UseCase에 전달한다.

```
Controller (Adapter)                    UseCase (Domain)
─────────────────────                   ────────────────
MultipartFile file
  → file.getOriginalFilename()   ──→    String originalFilename
  → file.getContentType()        ──→    String contentType
  → file.getSize()               ──→    long fileSize
  → file.getBytes()              ──→    byte[] data
```

- Controller가 `MultipartFile`에서 순수 Java 값(String, long, byte[])을 추출
- UseCase는 프레임워크 타입을 모른 채 순수 값만 받아 도메인 로직 수행
- 이를 통해 도메인 계층의 프레임워크 무의존 원칙을 유지

---

## 7. 설정 변경

### application.yml (공통)

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 5MB

image:
  storage:
    path: ./data/images
```

### SecurityConfig 변경

```java
.requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
```

---

## 8. 테스트 전략

### 도메인 테스트 (PostImageTest)
- 정상 생성
- 허용되지 않은 Content-Type 시 예외
- 파일 크기 초과 시 예외
- 빈 파일명 시 예외

### UseCase 테스트 (인수 테스트)
- **UploadImageUseCaseTest**: 업로드 성공, 게시글 없음 시 예외, 본인 글 아닐 때 예외, 5장 초과 시 예외
- **GetImageUseCaseTest**: 이미지 조회 성공, 없는 파일 시 예외
- **DeleteImageUseCaseTest**: 삭제 성공, 본인 글 아닐 때 예외, 게시글/이미지 없음 시 예외

### Fixture
- `PostImageFixture` — 테스트용 PostImage 객체 생성

---

## 9. 기존 API 변경 상세

### GetPostUseCase 변경
- `PostImageRepository`를 의존성으로 추가
- 게시글 조회 시 해당 게시글의 이미지 목록도 함께 조회
- `GetPostResponse`에 `images` 필드 추가

### DeletePostUseCase 변경
- `PostImageRepository`와 `ImageStorage`를 의존성으로 추가
- 게시글 삭제 시 연관 이미지 메타데이터 + 파일도 함께 삭제

---

## 10. 구현 순서

1. 도메인 엔티티 (`PostImage`) + 도메인 테스트
2. Port 인터페이스 (`PostImageRepository`, `ImageStorage`)
3. Fake 어댑터 (`FakePostImageRepository`, `FakeImageStorage`)
4. 이미지 업로드 UseCase + 테스트
5. 이미지 조회 UseCase + 테스트
6. 이미지 삭제 UseCase + 테스트
7. 기존 게시글 조회/삭제 UseCase 변경 + 테스트 수정
8. JDBC 어댑터 (`JdbcPostImageRepository`)
9. `LocalImageStorage` 구현
10. Flyway 마이그레이션 (`V3__add_post_image_table.sql`)
11. 설정 파일 변경 (multipart, security)
