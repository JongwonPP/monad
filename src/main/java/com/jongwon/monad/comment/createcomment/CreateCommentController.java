package com.jongwon.monad.comment.createcomment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateCommentController {

    private final CreateCommentUseCase createCommentUseCase;

    public CreateCommentController(CreateCommentUseCase createCommentUseCase) {
        this.createCommentUseCase = createCommentUseCase;
    }

    @PostMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<CreateCommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request) {
        CreateCommentResponse response = createCommentUseCase.execute(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
