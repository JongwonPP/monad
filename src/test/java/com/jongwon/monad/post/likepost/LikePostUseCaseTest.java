package com.jongwon.monad.post.likepost;

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

class LikePostUseCaseTest {

    private LikePostUseCase useCase;
    private PostRepository postRepository;
    private PostLikeRepository postLikeRepository;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        postLikeRepository = new FakePostLikeRepository();
        useCase = new LikePostUseCase(postRepository, postLikeRepository);
    }

    @Test
    void 게시글_좋아요_성공() {
        var post = PostFixture.create(1L);
        postRepository.save(post);

        LikePostResponse response = useCase.execute(post.getId(), 1L);

        assertThat(response.postId()).isEqualTo(post.getId());
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1L);
    }

    @Test
    void 존재하지_않는_게시글에_좋아요시_예외() {
        assertThatThrownBy(() -> useCase.execute(999L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }

    @Test
    void 이미_좋아요한_게시글에_다시_좋아요시_예외() {
        var post = PostFixture.create(1L);
        postRepository.save(post);

        var like = PostLikeFixture.create(post.getId(), 1L);
        postLikeRepository.save(like);

        assertThatThrownBy(() -> useCase.execute(post.getId(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 좋아요한 게시글입니다");
    }
}
