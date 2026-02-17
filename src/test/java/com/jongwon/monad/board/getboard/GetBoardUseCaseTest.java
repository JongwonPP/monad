package com.jongwon.monad.board.getboard;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.board.infra.FakeBoardRepository;
import com.jongwon.monad.fixture.BoardFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetBoardUseCaseTest {

    private GetBoardUseCase useCase;
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        boardRepository = new FakeBoardRepository();
        useCase = new GetBoardUseCase(boardRepository);
    }

    @Test
    void 게시판_조회_성공() {
        Board board = BoardFixture.create();
        boardRepository.save(board);

        GetBoardResponse response = useCase.execute(board.getId());

        assertThat(response.id()).isEqualTo(board.getId());
        assertThat(response.name()).isEqualTo("자유게시판");
        assertThat(response.description()).isEqualTo("자유롭게 글을 작성하세요");
    }

    @Test
    void 존재하지_않는_게시판_조회시_예외() {
        assertThatThrownBy(() -> useCase.execute(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
