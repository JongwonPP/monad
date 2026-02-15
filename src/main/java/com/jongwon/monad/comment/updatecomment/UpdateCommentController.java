package com.jongwon.monad.comment.updatecomment;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateCommentController {

    private final UpdateCommentUseCase updateCommentUseCase;

    public UpdateCommentController(UpdateCommentUseCase updateCommentUseCase) {
        this.updateCommentUseCase = updateCommentUseCase;
    }

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
