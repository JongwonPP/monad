package com.jongwon.monad.board.getboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board", description = "게시판 API")
@RestController
public class GetBoardController {

    private final GetBoardUseCase getBoardUseCase;

    public GetBoardController(GetBoardUseCase getBoardUseCase) {
        this.getBoardUseCase = getBoardUseCase;
    }

    @Operation(summary = "게시판 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @GetMapping("/api/v1/boards/{id}")
    public ResponseEntity<GetBoardResponse> getBoard(@PathVariable Long id) {
        GetBoardResponse response = getBoardUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}
