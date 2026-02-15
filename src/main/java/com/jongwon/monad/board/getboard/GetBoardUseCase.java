package com.jongwon.monad.board.getboard;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetBoardUseCase {

    private final BoardRepository boardRepository;

    public GetBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public GetBoardResponse execute(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다: " + id));

        return new GetBoardResponse(
                board.getId(),
                board.getName(),
                board.getDescription(),
                board.getCreatedAt()
        );
    }
}
