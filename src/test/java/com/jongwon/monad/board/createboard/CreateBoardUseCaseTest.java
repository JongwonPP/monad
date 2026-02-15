package com.jongwon.monad.board.createboard;

import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.board.fake.FakeBoardRepository;
import com.jongwon.monad.fixture.BoardFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateBoardUseCaseTest {

    private CreateBoardUseCase useCase;
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        boardRepository = new FakeBoardRepository();
        useCase = new CreateBoardUseCase(boardRepository);
    }

    @Test
    void 게시판_생성_성공() {
        CreateBoardRequest request = new CreateBoardRequest("자유게시판", "자유롭게 글을 작성하세요");

        CreateBoardResponse response = useCase.execute(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("자유게시판");
        assertThat(response.description()).isEqualTo("자유롭게 글을 작성하세요");
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void 중복_이름으로_생성시_예외() {
        boardRepository.save(BoardFixture.createWithName("자유게시판"));

        CreateBoardRequest request = new CreateBoardRequest("자유게시판", "설명");

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 게시판 이름");
    }
}
