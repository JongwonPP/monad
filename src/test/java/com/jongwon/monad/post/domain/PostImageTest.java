package com.jongwon.monad.post.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostImageTest {

    @Test
    void 정상_생성() {
        PostImage image = PostImage.create(1L, "photo.jpg", "uuid.jpg", "image/jpeg", 1024L);

        assertThat(image.getPostId()).isEqualTo(1L);
        assertThat(image.getOriginalFilename()).isEqualTo("photo.jpg");
        assertThat(image.getStoredFilename()).isEqualTo("uuid.jpg");
        assertThat(image.getContentType()).isEqualTo("image/jpeg");
        assertThat(image.getFileSize()).isEqualTo(1024L);
        assertThat(image.getCreatedAt()).isNotNull();
    }

    @Test
    void 허용되지_않은_contentType이면_예외() {
        assertThatThrownBy(() -> PostImage.create(1L, "file.txt", "uuid.txt", "text/plain", 1024L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("허용되지 않는 이미지 형식");
    }

    @Test
    void contentType_null이면_예외() {
        assertThatThrownBy(() -> PostImage.create(1L, "photo.jpg", "uuid.jpg", null, 1024L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("허용되지 않는 이미지 형식");
    }

    @Test
    void 파일_크기_초과시_예외() {
        long overSize = 5 * 1024 * 1024 + 1;
        assertThatThrownBy(() -> PostImage.create(1L, "photo.jpg", "uuid.jpg", "image/jpeg", overSize))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5MB를 초과");
    }

    @Test
    void 파일_크기_0이면_예외() {
        assertThatThrownBy(() -> PostImage.create(1L, "photo.jpg", "uuid.jpg", "image/jpeg", 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("0보다 커야");
    }

    @Test
    void 원본_파일명_빈값이면_예외() {
        assertThatThrownBy(() -> PostImage.create(1L, "", "uuid.jpg", "image/jpeg", 1024L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("원본 파일명은 빈 값");
    }

    @Test
    void 원본_파일명_null이면_예외() {
        assertThatThrownBy(() -> PostImage.create(1L, null, "uuid.jpg", "image/jpeg", 1024L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("원본 파일명은 빈 값");
    }

    @Test
    void 게시글ID_null이면_예외() {
        assertThatThrownBy(() -> PostImage.create(null, "photo.jpg", "uuid.jpg", "image/jpeg", 1024L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글 ID는 필수");
    }

    @Test
    void PNG_형식_정상_생성() {
        PostImage image = PostImage.create(1L, "photo.png", "uuid.png", "image/png", 2048L);
        assertThat(image.getContentType()).isEqualTo("image/png");
    }

    @Test
    void GIF_형식_정상_생성() {
        PostImage image = PostImage.create(1L, "anim.gif", "uuid.gif", "image/gif", 3072L);
        assertThat(image.getContentType()).isEqualTo("image/gif");
    }

    @Test
    void WEBP_형식_정상_생성() {
        PostImage image = PostImage.create(1L, "photo.webp", "uuid.webp", "image/webp", 1536L);
        assertThat(image.getContentType()).isEqualTo("image/webp");
    }
}
