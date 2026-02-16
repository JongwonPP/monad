package com.jongwon.monad.post.deletepost;

import com.jongwon.monad.fixture.PostFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.infra.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeletePostUseCaseTest {

    private DeletePostUseCase useCase;
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        useCase = new DeletePostUseCase(postRepository);
    }

    @Test
    void 게시글_삭제_성공() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        useCase.execute(post.getId(), 1L);

        assertThat(postRepository.findById(post.getId())).isEmpty();
    }

    @Test
    void 존재하지_않는_게시글_삭제시_예외() {
        assertThatThrownBy(() -> useCase.execute(999L, 1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 본인이_아닌_게시글_삭제시_예외() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        assertThatThrownBy(() -> useCase.execute(post.getId(), 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인의 글만 삭제할 수 있습니다");
    }
}
