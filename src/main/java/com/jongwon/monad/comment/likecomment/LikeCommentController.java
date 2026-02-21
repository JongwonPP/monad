package com.jongwon.monad.comment.likecomment;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment Like", description = "댓글 좋아요 API")
@RestController
public class LikeCommentController {

    private final LikeCommentUseCase likeCommentUseCase;

    public LikeCommentController(LikeCommentUseCase likeCommentUseCase) {
        this.likeCommentUseCase = likeCommentUseCase;
    }

    @Operation(summary = "댓글 좋아요")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "400", description = "이미 좋아요"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @PostMapping("/api/v1/comments/{commentId}/likes")
    public ResponseEntity<LikeCommentResponse> likeComment(
            @PathVariable Long commentId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        LikeCommentResponse response = likeCommentUseCase.execute(commentId, principal.memberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
