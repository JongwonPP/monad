package com.jongwon.monad.post.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostTest {

    @Test
    void 정상_생성() {
        Post post = Post.create(1L, "제목", "본문", "작성자");

        assertThat(post.getBoardId()).isEqualTo(1L);
        assertThat(post.getTitle()).isEqualTo("제목");
        assertThat(post.getContent()).isEqualTo("본문");
        assertThat(post.getAuthor()).isEqualTo("작성자");
        assertThat(post.getViewCount()).isZero();
        assertThat(post.getCreatedAt()).isNotNull();
        assertThat(post.getUpdatedAt()).isNotNull();
    }

    @Test
    void title_빈문자열이면_예외() {
        assertThatThrownBy(() -> Post.create(1L, "", "본문", "작성자"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void title_null이면_예외() {
        assertThatThrownBy(() -> Post.create(1L, null, "본문", "작성자"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void title_200자_초과면_예외() {
        String longTitle = "a".repeat(201);
        assertThatThrownBy(() -> Post.create(1L, longTitle, "본문", "작성자"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content_빈문자열이면_예외() {
        assertThatThrownBy(() -> Post.create(1L, "제목", "", "작성자"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 정상_수정() {
        Post post = Post.create(1L, "원래 제목", "원래 본문", "작성자");
        LocalDateTimeSnapshot before = new LocalDateTimeSnapshot(post.getUpdatedAt());

        post.update("수정 제목", "수정 본문");

        assertThat(post.getTitle()).isEqualTo("수정 제목");
        assertThat(post.getContent()).isEqualTo("수정 본문");
        assertThat(post.getUpdatedAt()).isAfterOrEqualTo(before.value());
    }

    @Test
    void 조회수_증가() {
        Post post = Post.create(1L, "제목", "본문", "작성자");
        assertThat(post.getViewCount()).isZero();

        post.increaseViewCount();

        assertThat(post.getViewCount()).isEqualTo(1);
    }

    private record LocalDateTimeSnapshot(java.time.LocalDateTime value) {}
}
