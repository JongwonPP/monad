package com.jongwon.monad.comment.createreply;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateReplyController {

    private final CreateReplyUseCase createReplyUseCase;

    public CreateReplyController(CreateReplyUseCase createReplyUseCase) {
        this.createReplyUseCase = createReplyUseCase;
    }

    @PostMapping("/api/v1/posts/{postId}/comments/{commentId}/replies")
    public ResponseEntity<CreateReplyResponse> createReply(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal,
            @Valid @RequestBody CreateReplyRequest request) {
        CreateReplyResponse response = createReplyUseCase.execute(postId, commentId, principal.memberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
