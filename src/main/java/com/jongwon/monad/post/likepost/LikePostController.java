package com.jongwon.monad.post.likepost;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikePostController {

    private final LikePostUseCase likePostUseCase;

    public LikePostController(LikePostUseCase likePostUseCase) {
        this.likePostUseCase = likePostUseCase;
    }

    @PostMapping("/api/v1/posts/{postId}/likes")
    public ResponseEntity<LikePostResponse> likePost(
            @PathVariable Long postId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        LikePostResponse response = likePostUseCase.execute(postId, principal.memberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
