package com.jongwon.monad.comment.deletecomment;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteCommentController {

    private final DeleteCommentUseCase deleteCommentUseCase;

    public DeleteCommentController(DeleteCommentUseCase deleteCommentUseCase) {
        this.deleteCommentUseCase = deleteCommentUseCase;
    }

    @DeleteMapping("/api/v1/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        deleteCommentUseCase.execute(commentId, principal.memberId());
        return ResponseEntity.noContent().build();
    }
}
