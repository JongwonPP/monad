package com.jongwon.monad.post.createpost;

import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.board.infra.FakeBoardRepository;
import com.jongwon.monad.fixture.BoardFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.infra.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreatePostUseCaseTest {

    private CreatePostUseCase useCase;
    private PostRepository postRepository;
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        boardRepository = new FakeBoardRepository();
        useCase = new CreatePostUseCase(postRepository, boardRepository);
    }

    @Test
    void 게시글_작성_성공() {
        var board = BoardFixture.create();
        boardRepository.save(board);

        CreatePostRequest request = new CreatePostRequest("제목", "본문");
        CreatePostResponse response = useCase.execute(board.getId(), 1L, request);

        assertThat(response.id()).isNotNull();
        assertThat(response.boardId()).isEqualTo(board.getId());
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.content()).isEqualTo("본문");
        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void 존재하지_않는_게시판에_작성시_예외() {
        CreatePostRequest request = new CreatePostRequest("제목", "본문");

        assertThatThrownBy(() -> useCase.execute(999L, 1L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시판을 찾을 수 없습니다");
    }
}
