package com.jongwon.monad.comment.unlikecomment;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment Like", description = "댓글 좋아요 API")
@RestController
public class UnlikeCommentController {

    private final UnlikeCommentUseCase unlikeCommentUseCase;

    public UnlikeCommentController(UnlikeCommentUseCase unlikeCommentUseCase) {
        this.unlikeCommentUseCase = unlikeCommentUseCase;
    }

    @Operation(summary = "댓글 좋아요 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "좋아요하지 않음"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @DeleteMapping("/api/v1/comments/{commentId}/likes")
    public ResponseEntity<UnlikeCommentResponse> unlikeComment(
            @PathVariable Long commentId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        UnlikeCommentResponse response = unlikeCommentUseCase.execute(commentId, principal.memberId());
        return ResponseEntity.ok(response);
    }
}
