package com.jongwon.monad.post.deleteimage;

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

class DeleteImageUseCaseTest {

    private DeleteImageUseCase useCase;
    private PostRepository postRepository;
    private PostImageRepository postImageRepository;
    private ImageStorage imageStorage;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        postImageRepository = new FakePostImageRepository();
        imageStorage = new FakeImageStorage();
        useCase = new DeleteImageUseCase(postRepository, postImageRepository, imageStorage);
    }

    @Test
    void 이미지_삭제_성공() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        PostImage postImage = PostImageFixture.create(post.getId());
        postImageRepository.save(postImage);
        imageStorage.store(postImage.getStoredFilename(), new byte[]{1, 2, 3});

        useCase.execute(post.getId(), postImage.getId(), 1L);

        assertThat(postImageRepository.findById(postImage.getId())).isEmpty();
        assertThatThrownBy(() -> imageStorage.load(postImage.getStoredFilename()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 존재하지_않는_게시글의_이미지_삭제시_예외() {
        assertThatThrownBy(() -> useCase.execute(999L, 1L, 1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 본인_글이_아닌_게시글의_이미지_삭제시_예외() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        assertThatThrownBy(() -> useCase.execute(post.getId(), 1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인의 게시글 이미지만 삭제할 수 있습니다");
    }

    @Test
    void 존재하지_않는_이미지_삭제시_예외() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        assertThatThrownBy(() -> useCase.execute(post.getId(), 999L, 1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 해당_게시글의_이미지가_아닌_경우_예외() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        PostImage postImage = PostImageFixture.create(999L);
        postImageRepository.save(postImage);

        assertThatThrownBy(() -> useCase.execute(post.getId(), postImage.getId(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 게시글의 이미지가 아닙니다");
    }
}
