package com.jongwon.monad.post.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostLikeTest {

    @Test
    void 게시글_좋아요_생성_성공() {
        PostLike postLike = PostLike.create(1L, 2L);

        assertThat(postLike.getPostId()).isEqualTo(1L);
        assertThat(postLike.getMemberId()).isEqualTo(2L);
        assertThat(postLike.getCreatedAt()).isNotNull();
    }

    @Test
    void 게시글ID가_null이면_예외() {
        assertThatThrownBy(() -> PostLike.create(null, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글 ID는 필수입니다");
    }

    @Test
    void 회원ID가_null이면_예외() {
        assertThatThrownBy(() -> PostLike.create(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원 ID는 필수입니다");
    }

    @Test
    void reconstruct로_복원() {
        PostLike postLike = PostLike.reconstruct(1L, 2L, 3L, java.time.LocalDateTime.now());

        assertThat(postLike.getId()).isEqualTo(1L);
        assertThat(postLike.getPostId()).isEqualTo(2L);
        assertThat(postLike.getMemberId()).isEqualTo(3L);
    }
}
