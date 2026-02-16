package com.jongwon.monad.comment.deletecomment;

import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.comment.infra.FakeCommentRepository;
import com.jongwon.monad.fixture.CommentFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteCommentUseCaseTest {

    private DeleteCommentUseCase useCase;
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository = new FakeCommentRepository();
        useCase = new DeleteCommentUseCase(commentRepository);
    }

    @Test
    void 댓글_삭제_성공_대댓글도_함께_삭제() {
        var parent = CommentFixture.create(1L);
        commentRepository.save(parent);

        var reply = CommentFixture.createReply(1L, parent.getId());
        commentRepository.save(reply);

        useCase.execute(parent.getId(), 1L);

        assertThat(commentRepository.findById(parent.getId())).isEmpty();
        assertThat(commentRepository.findById(reply.getId())).isEmpty();
    }

    @Test
    void 존재하지_않는_댓글_삭제시_예외() {
        assertThatThrownBy(() -> useCase.execute(999L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("댓글을 찾을 수 없습니다");
    }

    @Test
    void 본인이_아닌_댓글_삭제시_예외() {
        var comment = CommentFixture.create(1L);
        commentRepository.save(comment);

        assertThatThrownBy(() -> useCase.execute(comment.getId(), 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인의 댓글만 삭제할 수 있습니다");
    }
}
