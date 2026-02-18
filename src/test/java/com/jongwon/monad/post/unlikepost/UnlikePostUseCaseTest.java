package com.jongwon.monad.post.unlikepost;

import com.jongwon.monad.fixture.PostFixture;
import com.jongwon.monad.fixture.PostLikeFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.PostLikeRepository;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.infra.FakePostLikeRepository;
import com.jongwon.monad.post.infra.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnlikePostUseCaseTest {

    private UnlikePostUseCase useCase;
    private PostRepository postRepository;
    private PostLikeRepository postLikeRepository;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        postLikeRepository = new FakePostLikeRepository();
        useCase = new UnlikePostUseCase(postRepository, postLikeRepository);
    }

    @Test
    void 게시글_좋아요_취소_성공() {
        var post = PostFixture.create(1L);
        postRepository.save(post);

        var like = PostLikeFixture.create(post.getId(), 1L);
        postLikeRepository.save(like);

        UnlikePostResponse response = useCase.execute(post.getId(), 1L);

        assertThat(response.postId()).isEqualTo(post.getId());
        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isEqualTo(0L);
        assertThat(postLikeRepository.findByPostIdAndMemberId(post.getId(), 1L)).isEmpty();
    }

    @Test
    void 존재하지_않는_게시글_좋아요_취소시_예외() {
        assertThatThrownBy(() -> useCase.execute(999L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }

    @Test
    void 좋아요하지_않은_게시글_취소시_예외() {
        var post = PostFixture.create(1L);
        postRepository.save(post);

        assertThatThrownBy(() -> useCase.execute(post.getId(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("좋아요하지 않은 게시글입니다");
    }
}
