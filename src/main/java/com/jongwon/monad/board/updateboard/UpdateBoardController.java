package com.jongwon.monad.board.updateboard;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateBoardController {

    private final UpdateBoardUseCase updateBoardUseCase;

    public UpdateBoardController(UpdateBoardUseCase updateBoardUseCase) {
        this.updateBoardUseCase = updateBoardUseCase;
    }

    @PutMapping("/api/v1/boards/{id}")
    public ResponseEntity<UpdateBoardResponse> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBoardRequest request) {
        UpdateBoardResponse response = updateBoardUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }
}
