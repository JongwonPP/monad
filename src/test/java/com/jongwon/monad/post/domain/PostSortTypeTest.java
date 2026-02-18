package com.jongwon.monad.post.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostSortTypeTest {

    @Test
    void from_latest() {
        assertThat(PostSortType.from("latest")).isEqualTo(PostSortType.LATEST);
    }

    @Test
    void from_대소문자_무시() {
        assertThat(PostSortType.from("VIEWS")).isEqualTo(PostSortType.VIEWS);
        assertThat(PostSortType.from("views")).isEqualTo(PostSortType.VIEWS);
        assertThat(PostSortType.from("Views")).isEqualTo(PostSortType.VIEWS);
    }

    @Test
    void from_null이면_기본값() {
        assertThat(PostSortType.from(null)).isEqualTo(PostSortType.LATEST);
    }

    @Test
    void from_빈문자열이면_기본값() {
        assertThat(PostSortType.from("")).isEqualTo(PostSortType.LATEST);
        assertThat(PostSortType.from("   ")).isEqualTo(PostSortType.LATEST);
    }

    @Test
    void from_유효하지_않은_값이면_예외() {
        assertThatThrownBy(() -> PostSortType.from("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
