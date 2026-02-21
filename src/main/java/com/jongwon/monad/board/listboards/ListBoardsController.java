package com.jongwon.monad.board.listboards;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Board", description = "게시판 API")
@RestController
public class ListBoardsController {

    private final ListBoardsUseCase listBoardsUseCase;

    public ListBoardsController(ListBoardsUseCase listBoardsUseCase) {
        this.listBoardsUseCase = listBoardsUseCase;
    }

    @Operation(summary = "게시판 목록")
    @GetMapping("/api/v1/boards")
    public ResponseEntity<List<ListBoardsResponse>> listBoards() {
        List<ListBoardsResponse> response = listBoardsUseCase.execute();
        return ResponseEntity.ok(response);
    }
}
