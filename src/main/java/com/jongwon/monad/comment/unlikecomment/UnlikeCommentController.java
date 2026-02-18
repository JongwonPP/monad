package com.jongwon.monad.comment.unlikecomment;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnlikeCommentController {

    private final UnlikeCommentUseCase unlikeCommentUseCase;

    public UnlikeCommentController(UnlikeCommentUseCase unlikeCommentUseCase) {
        this.unlikeCommentUseCase = unlikeCommentUseCase;
    }

    @DeleteMapping("/api/v1/comments/{commentId}/likes")
    public ResponseEntity<UnlikeCommentResponse> unlikeComment(
            @PathVariable Long commentId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        UnlikeCommentResponse response = unlikeCommentUseCase.execute(commentId, principal.memberId());
        return ResponseEntity.ok(response);
    }
}
