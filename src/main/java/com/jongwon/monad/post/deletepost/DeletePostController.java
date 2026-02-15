package com.jongwon.monad.post.deletepost;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeletePostController {

    private final DeletePostUseCase deletePostUseCase;

    public DeletePostController(DeletePostUseCase deletePostUseCase) {
        this.deletePostUseCase = deletePostUseCase;
    }

    @DeleteMapping("/api/v1/boards/{boardId}/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        deletePostUseCase.execute(postId, principal.memberId());
        return ResponseEntity.noContent().build();
    }
}
