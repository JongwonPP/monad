package com.jongwon.monad.post.createpost;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post", description = "게시글 API")
@RestController
public class CreatePostController {

    private final CreatePostUseCase createPostUseCase;

    public CreatePostController(CreatePostUseCase createPostUseCase) {
        this.createPostUseCase = createPostUseCase;
    }

    @Operation(summary = "게시글 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @PostMapping("/api/v1/boards/{boardId}/posts")
    public ResponseEntity<CreatePostResponse> createPost(
            @PathVariable Long boardId,
            @Valid @RequestBody CreatePostRequest request,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        CreatePostResponse response = createPostUseCase.execute(boardId, principal.memberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
