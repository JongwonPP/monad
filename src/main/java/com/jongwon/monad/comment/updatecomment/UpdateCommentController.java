package com.jongwon.monad.comment.updatecomment;

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
            @Valid @RequestBody UpdateCommentRequest request) {
        UpdateCommentResponse response = updateCommentUseCase.execute(commentId, request);
        return ResponseEntity.ok(response);
    }
}
