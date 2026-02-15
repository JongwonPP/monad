package com.jongwon.monad.board.deleteboard;

import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DeleteBoardUseCase {

    private final BoardRepository boardRepository;

    public DeleteBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public void execute(Long id) {
        boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다: " + id));

        boardRepository.deleteById(id);
    }
}
