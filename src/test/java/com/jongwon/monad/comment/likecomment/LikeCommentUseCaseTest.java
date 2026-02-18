package com.jongwon.monad.comment.likecomment;

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

class LikeCommentUseCaseTest {

    private LikeCommentUseCase useCase;
    private CommentRepository commentRepository;
    private CommentLikeRepository commentLikeRepository;

    @BeforeEach
    void setUp() {
        commentRepository = new FakeCommentRepository();
        commentLikeRepository = new FakeCommentLikeRepository();
        useCase = new LikeCommentUseCase(commentRepository, commentLikeRepository);
    }

    @Test
    void 댓글_좋아요_성공() {
        var comment = CommentFixture.create(1L);
        commentRepository.save(comment);

        LikeCommentResponse response = useCase.execute(comment.getId(), 1L);

        assertThat(response.commentId()).isEqualTo(comment.getId());
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1L);
    }

    @Test
    void 존재하지_않는_댓글에_좋아요시_예외() {
        assertThatThrownBy(() -> useCase.execute(999L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("댓글을 찾을 수 없습니다");
    }

    @Test
    void 이미_좋아요한_댓글에_다시_좋아요시_예외() {
        var comment = CommentFixture.create(1L);
        commentRepository.save(comment);

        var like = CommentLikeFixture.create(comment.getId(), 1L);
        commentLikeRepository.save(like);

        assertThatThrownBy(() -> useCase.execute(comment.getId(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 좋아요한 댓글입니다");
    }
}
