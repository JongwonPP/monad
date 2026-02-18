package com.jongwon.monad.comment.likecomment;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikeCommentController {

    private final LikeCommentUseCase likeCommentUseCase;

    public LikeCommentController(LikeCommentUseCase likeCommentUseCase) {
        this.likeCommentUseCase = likeCommentUseCase;
    }

    @PostMapping("/api/v1/comments/{commentId}/likes")
    public ResponseEntity<LikeCommentResponse> likeComment(
            @PathVariable Long commentId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        LikeCommentResponse response = likeCommentUseCase.execute(commentId, principal.memberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
