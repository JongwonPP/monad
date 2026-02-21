package com.jongwon.monad.post.getimage;

import com.jongwon.monad.fixture.PostImageFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.ImageStorage;
import com.jongwon.monad.post.domain.PostImage;
import com.jongwon.monad.post.domain.PostImageRepository;
import com.jongwon.monad.post.infra.FakeImageStorage;
import com.jongwon.monad.post.infra.FakePostImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetImageUseCaseTest {

    private GetImageUseCase useCase;
    private PostImageRepository postImageRepository;
    private ImageStorage imageStorage;

    @BeforeEach
    void setUp() {
        postImageRepository = new FakePostImageRepository();
        imageStorage = new FakeImageStorage();
        useCase = new GetImageUseCase(postImageRepository, imageStorage);
    }

    @Test
    void 이미지_조회_성공() {
        PostImage postImage = PostImageFixture.create(1L);
        postImageRepository.save(postImage);
        byte[] imageData = new byte[]{1, 2, 3, 4, 5};
        imageStorage.store(postImage.getStoredFilename(), imageData);

        GetImageUseCase.ImageData result = useCase.execute(postImage.getStoredFilename());

        assertThat(result.data()).isEqualTo(imageData);
        assertThat(result.contentType()).isEqualTo("image/jpeg");
    }

    @Test
    void 존재하지_않는_이미지_조회시_예외() {
        assertThatThrownBy(() -> useCase.execute("non-existent.jpg"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
