package com.jongwon.monad.board.listboards;

import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.board.fake.FakeBoardRepository;
import com.jongwon.monad.fixture.BoardFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListBoardsUseCaseTest {

    private ListBoardsUseCase useCase;
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        boardRepository = new FakeBoardRepository();
        useCase = new ListBoardsUseCase(boardRepository);
    }

    @Test
    void 게시판_목록_조회() {
        boardRepository.save(BoardFixture.createWithName("자유게시판"));
        boardRepository.save(BoardFixture.createWithName("공지게시판"));

        List<ListBoardsResponse> response = useCase.execute();

        assertThat(response).hasSize(2);
    }

    @Test
    void 게시판이_없으면_빈_목록() {
        List<ListBoardsResponse> response = useCase.execute();

        assertThat(response).isEmpty();
    }
}
