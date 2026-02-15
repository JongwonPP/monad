package com.jongwon.monad.board.updateboard;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UpdateBoardUseCase {

    private final BoardRepository boardRepository;

    public UpdateBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public UpdateBoardResponse execute(Long id, UpdateBoardRequest request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다: " + id));

        board.update(request.name(), request.description());
        Board saved = boardRepository.save(board);

        return new UpdateBoardResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getUpdatedAt()
        );
    }
}
