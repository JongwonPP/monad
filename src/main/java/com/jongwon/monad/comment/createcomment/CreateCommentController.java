package com.jongwon.monad.comment.createcomment;

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

@Tag(name = "Comment", description = "댓글 API")
@RestController
public class CreateCommentController {

    private final CreateCommentUseCase createCommentUseCase;

    public CreateCommentController(CreateCommentUseCase createCommentUseCase) {
        this.createCommentUseCase = createCommentUseCase;
    }

    @Operation(summary = "댓글 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PostMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<CreateCommentResponse> createComment(
            @PathVariable Long postId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal,
            @Valid @RequestBody CreateCommentRequest request) {
        CreateCommentResponse response = createCommentUseCase.execute(postId, principal.memberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
