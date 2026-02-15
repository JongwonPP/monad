package com.jongwon.monad.board.createboard;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateBoardUseCase {

    private final BoardRepository boardRepository;

    public CreateBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public CreateBoardResponse execute(CreateBoardRequest request) {
        if (boardRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("이미 존재하는 게시판 이름입니다: " + request.name());
        }

        Board board = Board.create(request.name(), request.description());
        Board saved = boardRepository.save(board);

        return new CreateBoardResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getCreatedAt()
        );
    }
}
