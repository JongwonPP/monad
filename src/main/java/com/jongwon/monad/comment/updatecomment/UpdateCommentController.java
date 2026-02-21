package com.jongwon.monad.comment.updatecomment;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
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

@Tag(name = "Comment", description = "댓글 API")
@RestController
public class UpdateCommentController {

    private final UpdateCommentUseCase updateCommentUseCase;

    public UpdateCommentController(UpdateCommentUseCase updateCommentUseCase) {
        this.updateCommentUseCase = updateCommentUseCase;
    }

    @Operation(summary = "댓글 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "본인 댓글만"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @PutMapping("/api/v1/posts/{postId}/comments/{commentId}")
    public ResponseEntity<UpdateCommentResponse> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal,
            @Valid @RequestBody UpdateCommentRequest request) {
        UpdateCommentResponse response = updateCommentUseCase.execute(commentId, principal.memberId(), request);
        return ResponseEntity.ok(response);
    }
}
