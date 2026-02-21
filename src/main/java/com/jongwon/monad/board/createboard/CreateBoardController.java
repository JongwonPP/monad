package com.jongwon.monad.board.createboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board", description = "게시판 API")
@RestController
public class CreateBoardController {

    private final CreateBoardUseCase createBoardUseCase;

    public CreateBoardController(CreateBoardUseCase createBoardUseCase) {
        this.createBoardUseCase = createBoardUseCase;
    }

    @Operation(summary = "게시판 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "409", description = "이름 중복")
    })
    @PostMapping("/api/v1/boards")
    public ResponseEntity<CreateBoardResponse> createBoard(@Valid @RequestBody CreateBoardRequest request) {
        CreateBoardResponse response = createBoardUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
