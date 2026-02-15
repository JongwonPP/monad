package com.jongwon.monad.board.deleteboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteBoardController {

    private final DeleteBoardUseCase deleteBoardUseCase;

    public DeleteBoardController(DeleteBoardUseCase deleteBoardUseCase) {
        this.deleteBoardUseCase = deleteBoardUseCase;
    }

    @DeleteMapping("/api/v1/boards/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        deleteBoardUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
