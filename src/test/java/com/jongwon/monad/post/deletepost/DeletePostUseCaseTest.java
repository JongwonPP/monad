package com.jongwon.monad.post.deletepost;

import com.jongwon.monad.fixture.PostFixture;
import com.jongwon.monad.fixture.PostImageFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.ImageStorage;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostImage;
import com.jongwon.monad.post.domain.PostImageRepository;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.infra.FakeImageStorage;
import com.jongwon.monad.post.infra.FakePostImageRepository;
import com.jongwon.monad.post.infra.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeletePostUseCaseTest {

    private DeletePostUseCase useCase;
    private PostRepository postRepository;
    private PostImageRepository postImageRepository;
    private ImageStorage imageStorage;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        postImageRepository = new FakePostImageRepository();
        imageStorage = new FakeImageStorage();
        useCase = new DeletePostUseCase(postRepository, postImageRepository, imageStorage);
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

    @Test
    void 게시글_삭제시_이미지도_함께_삭제() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        PostImage image = PostImageFixture.create(post.getId());
        postImageRepository.save(image);
        imageStorage.store(image.getStoredFilename(), new byte[]{1, 2, 3});

        useCase.execute(post.getId(), 1L);

        assertThat(postRepository.findById(post.getId())).isEmpty();
        assertThat(postImageRepository.findAllByPostId(post.getId())).isEmpty();
        assertThatThrownBy(() -> imageStorage.load(image.getStoredFilename()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
