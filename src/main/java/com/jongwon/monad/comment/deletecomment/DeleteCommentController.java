package com.jongwon.monad.comment.deletecomment;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment", description = "댓글 API")
@RestController
public class DeleteCommentController {

    private final DeleteCommentUseCase deleteCommentUseCase;

    public DeleteCommentController(DeleteCommentUseCase deleteCommentUseCase) {
        this.deleteCommentUseCase = deleteCommentUseCase;
    }

    @Operation(summary = "댓글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공"),
            @ApiResponse(responseCode = "403", description = "본인 댓글만"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @DeleteMapping("/api/v1/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        deleteCommentUseCase.execute(commentId, principal.memberId());
        return ResponseEntity.noContent().build();
    }
}
