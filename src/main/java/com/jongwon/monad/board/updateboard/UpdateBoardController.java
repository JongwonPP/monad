package com.jongwon.monad.board.updateboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board", description = "게시판 API")
@RestController
public class UpdateBoardController {

    private final UpdateBoardUseCase updateBoardUseCase;

    public UpdateBoardController(UpdateBoardUseCase updateBoardUseCase) {
        this.updateBoardUseCase = updateBoardUseCase;
    }

    @Operation(summary = "게시판 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @PutMapping("/api/v1/boards/{id}")
    public ResponseEntity<UpdateBoardResponse> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBoardRequest request) {
        UpdateBoardResponse response = updateBoardUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }
}
