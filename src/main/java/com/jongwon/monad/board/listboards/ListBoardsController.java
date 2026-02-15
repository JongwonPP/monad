package com.jongwon.monad.board.listboards;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ListBoardsController {

    private final ListBoardsUseCase listBoardsUseCase;

    public ListBoardsController(ListBoardsUseCase listBoardsUseCase) {
        this.listBoardsUseCase = listBoardsUseCase;
    }

    @GetMapping("/api/v1/boards")
    public ResponseEntity<List<ListBoardsResponse>> listBoards() {
        List<ListBoardsResponse> response = listBoardsUseCase.execute();
        return ResponseEntity.ok(response);
    }
}
