package com.jongwon.monad.post.updatepost;

import com.jongwon.monad.fixture.PostFixture;
import com.jongwon.monad.global.exception.AuthorizationException;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.infra.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdatePostUseCaseTest {

    private UpdatePostUseCase useCase;
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        useCase = new UpdatePostUseCase(postRepository);
    }

    @Test
    void 게시글_수정_성공() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        UpdatePostRequest request = new UpdatePostRequest("수정된 제목", "수정된 본문");
        UpdatePostResponse response = useCase.execute(post.getId(), 1L, request);

        assertThat(response.id()).isEqualTo(post.getId());
        assertThat(response.title()).isEqualTo("수정된 제목");
        assertThat(response.content()).isEqualTo("수정된 본문");
        assertThat(response.updatedAt()).isNotNull();
    }

    @Test
    void 존재하지_않는_게시글_수정시_예외() {
        UpdatePostRequest request = new UpdatePostRequest("제목", "본문");

        assertThatThrownBy(() -> useCase.execute(999L, 1L, request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 본인이_아닌_게시글_수정시_예외() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        UpdatePostRequest request = new UpdatePostRequest("수정된 제목", "수정된 본문");

        assertThatThrownBy(() -> useCase.execute(post.getId(), 2L, request))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("본인의 글만 수정할 수 있습니다");
    }
}
