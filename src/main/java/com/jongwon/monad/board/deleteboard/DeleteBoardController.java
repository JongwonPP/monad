package com.jongwon.monad.board.deleteboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board", description = "게시판 API")
@RestController
public class DeleteBoardController {

    private final DeleteBoardUseCase deleteBoardUseCase;

    public DeleteBoardController(DeleteBoardUseCase deleteBoardUseCase) {
        this.deleteBoardUseCase = deleteBoardUseCase;
    }

    @Operation(summary = "게시판 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @DeleteMapping("/api/v1/boards/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        deleteBoardUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
