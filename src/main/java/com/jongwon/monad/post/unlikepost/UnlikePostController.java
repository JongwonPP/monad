package com.jongwon.monad.post.unlikepost;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnlikePostController {

    private final UnlikePostUseCase unlikePostUseCase;

    public UnlikePostController(UnlikePostUseCase unlikePostUseCase) {
        this.unlikePostUseCase = unlikePostUseCase;
    }

    @DeleteMapping("/api/v1/posts/{postId}/likes")
    public ResponseEntity<UnlikePostResponse> unlikePost(
            @PathVariable Long postId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        UnlikePostResponse response = unlikePostUseCase.execute(postId, principal.memberId());
        return ResponseEntity.ok(response);
    }
}
