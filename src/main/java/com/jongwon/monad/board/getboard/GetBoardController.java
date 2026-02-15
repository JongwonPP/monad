package com.jongwon.monad.board.getboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetBoardController {

    private final GetBoardUseCase getBoardUseCase;

    public GetBoardController(GetBoardUseCase getBoardUseCase) {
        this.getBoardUseCase = getBoardUseCase;
    }

    @GetMapping("/api/v1/boards/{id}")
    public ResponseEntity<GetBoardResponse> getBoard(@PathVariable Long id) {
        GetBoardResponse response = getBoardUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}
