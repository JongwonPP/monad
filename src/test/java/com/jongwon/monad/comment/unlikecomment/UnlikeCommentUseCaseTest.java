package com.jongwon.monad.comment.unlikecomment;

import com.jongwon.monad.comment.domain.CommentLikeRepository;
import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.comment.infra.FakeCommentLikeRepository;
import com.jongwon.monad.comment.infra.FakeCommentRepository;
import com.jongwon.monad.fixture.CommentFixture;
import com.jongwon.monad.fixture.CommentLikeFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnlikeCommentUseCaseTest {

    private UnlikeCommentUseCase useCase;
    private CommentRepository commentRepository;
    private CommentLikeRepository commentLikeRepository;

    @BeforeEach
    void setUp() {
        commentRepository = new FakeCommentRepository();
        commentLikeRepository = new FakeCommentLikeRepository();
        useCase = new UnlikeCommentUseCase(commentRepository, commentLikeRepository);
    }

    @Test
    void 댓글_좋아요_취소_성공() {
        var comment = CommentFixture.create(1L);
        commentRepository.save(comment);

        var like = CommentLikeFixture.create(comment.getId(), 1L);
        commentLikeRepository.save(like);

        UnlikeCommentResponse response = useCase.execute(comment.getId(), 1L);

        assertThat(response.commentId()).isEqualTo(comment.getId());
        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isEqualTo(0L);
        assertThat(commentLikeRepository.findByCommentIdAndMemberId(comment.getId(), 1L)).isEmpty();
    }

    @Test
    void 존재하지_않는_댓글_좋아요_취소시_예외() {
        assertThatThrownBy(() -> useCase.execute(999L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("댓글을 찾을 수 없습니다");
    }

    @Test
    void 좋아요하지_않은_댓글_취소시_예외() {
        var comment = CommentFixture.create(1L);
        commentRepository.save(comment);

        assertThatThrownBy(() -> useCase.execute(comment.getId(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("좋아요하지 않은 댓글입니다");
    }
}
