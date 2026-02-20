package com.jongwon.monad.post.uploadimage;

import com.jongwon.monad.fixture.PostFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.ImageStorage;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostImageRepository;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.infra.FakeImageStorage;
import com.jongwon.monad.post.infra.FakePostImageRepository;
import com.jongwon.monad.post.infra.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UploadImageUseCaseTest {

    private UploadImageUseCase useCase;
    private PostRepository postRepository;
    private PostImageRepository postImageRepository;
    private ImageStorage imageStorage;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        postImageRepository = new FakePostImageRepository();
        imageStorage = new FakeImageStorage();
        useCase = new UploadImageUseCase(postRepository, postImageRepository, imageStorage);
    }

    @Test
    void 이미지_업로드_성공() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        UploadImageResponse response = useCase.execute(
                post.getId(), post.getMemberId(),
                "test.jpg", "image/jpeg", 1024L, new byte[]{1, 2, 3}
        );

        assertThat(response.id()).isNotNull();
        assertThat(response.postId()).isEqualTo(post.getId());
        assertThat(response.originalFilename()).isEqualTo("test.jpg");
        assertThat(response.storedFilename()).endsWith(".jpg");
        assertThat(response.imageUrl()).startsWith("/api/v1/images/");
        assertThat(response.fileSize()).isEqualTo(1024L);
        assertThat(response.contentType()).isEqualTo("image/jpeg");
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void 존재하지_않는_게시글에_이미지_업로드시_예외() {
        assertThatThrownBy(() -> useCase.execute(
                999L, 1L, "test.jpg", "image/jpeg", 1024L, new byte[]{1, 2, 3}
        ))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }

    @Test
    void 본인_글이_아닌_게시글에_이미지_업로드시_예외() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        assertThatThrownBy(() -> useCase.execute(
                post.getId(), 999L, "test.jpg", "image/jpeg", 1024L, new byte[]{1, 2, 3}
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인의 게시글만 이미지를 첨부할 수 있습니다");
    }

    @Test
    void 이미지_5장_초과시_예외() {
        Post post = PostFixture.create(1L);
        postRepository.save(post);

        for (int i = 0; i < 5; i++) {
            useCase.execute(
                    post.getId(), post.getMemberId(),
                    "test" + i + ".jpg", "image/jpeg", 1024L, new byte[]{1, 2, 3}
            );
        }

        assertThatThrownBy(() -> useCase.execute(
                post.getId(), post.getMemberId(),
                "test5.jpg", "image/jpeg", 1024L, new byte[]{1, 2, 3}
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글 당 이미지는 최대 5장까지 첨부할 수 있습니다");
    }
}
