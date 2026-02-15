package com.jongwon.monad.board.listboards;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListBoardsUseCase {

    private final BoardRepository boardRepository;

    public ListBoardsUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<ListBoardsResponse> execute() {
        return boardRepository.findAll().stream()
                .map(board -> new ListBoardsResponse(
                        board.getId(),
                        board.getName(),
                        board.getDescription()
                ))
                .toList();
    }
}
