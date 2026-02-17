package com.jongwon.monad.board.deleteboard;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.board.infra.FakeBoardRepository;
import com.jongwon.monad.fixture.BoardFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteBoardUseCaseTest {

    private DeleteBoardUseCase useCase;
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        boardRepository = new FakeBoardRepository();
        useCase = new DeleteBoardUseCase(boardRepository);
    }

    @Test
    void 게시판_삭제_성공() {
        Board board = BoardFixture.create();
        boardRepository.save(board);

        useCase.execute(board.getId());

        assertThat(boardRepository.findById(board.getId())).isEmpty();
    }

    @Test
    void 존재하지_않는_게시판_삭제시_예외() {
        assertThatThrownBy(() -> useCase.execute(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
