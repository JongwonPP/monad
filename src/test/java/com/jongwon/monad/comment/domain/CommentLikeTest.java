package com.jongwon.monad.comment.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentLikeTest {

    @Test
    void 댓글_좋아요_생성_성공() {
        CommentLike commentLike = CommentLike.create(1L, 2L);

        assertThat(commentLike.getCommentId()).isEqualTo(1L);
        assertThat(commentLike.getMemberId()).isEqualTo(2L);
        assertThat(commentLike.getCreatedAt()).isNotNull();
    }

    @Test
    void 댓글ID가_null이면_예외() {
        assertThatThrownBy(() -> CommentLike.create(null, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("댓글 ID는 필수입니다");
    }

    @Test
    void 회원ID가_null이면_예외() {
        assertThatThrownBy(() -> CommentLike.create(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원 ID는 필수입니다");
    }

    @Test
    void reconstruct로_복원() {
        CommentLike commentLike = CommentLike.reconstruct(1L, 2L, 3L, java.time.LocalDateTime.now());

        assertThat(commentLike.getId()).isEqualTo(1L);
        assertThat(commentLike.getCommentId()).isEqualTo(2L);
        assertThat(commentLike.getMemberId()).isEqualTo(3L);
    }
}
