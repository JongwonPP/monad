package com.jongwon.monad.board.updateboard;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.board.infra.FakeBoardRepository;
import com.jongwon.monad.fixture.BoardFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateBoardUseCaseTest {

    private UpdateBoardUseCase useCase;
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        boardRepository = new FakeBoardRepository();
        useCase = new UpdateBoardUseCase(boardRepository);
    }

    @Test
    void 게시판_수정_성공() {
        Board board = BoardFixture.create();
        boardRepository.save(board);

        UpdateBoardRequest request = new UpdateBoardRequest("공지게시판", "공지사항을 올리는 게시판");
        UpdateBoardResponse response = useCase.execute(board.getId(), request);

        assertThat(response.id()).isEqualTo(board.getId());
        assertThat(response.name()).isEqualTo("공지게시판");
        assertThat(response.description()).isEqualTo("공지사항을 올리는 게시판");
        assertThat(response.updatedAt()).isNotNull();
    }

    @Test
    void 존재하지_않는_게시판_수정시_예외() {
        UpdateBoardRequest request = new UpdateBoardRequest("공지게시판", "설명");

        assertThatThrownBy(() -> useCase.execute(999L, request))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
