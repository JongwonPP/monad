package com.jongwon.monad.post.deleteimage;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteImageController {

    private final DeleteImageUseCase deleteImageUseCase;

    public DeleteImageController(DeleteImageUseCase deleteImageUseCase) {
        this.deleteImageUseCase = deleteImageUseCase;
    }

    @DeleteMapping("/api/v1/posts/{postId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long postId,
            @PathVariable Long imageId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        deleteImageUseCase.execute(postId, imageId, principal.memberId());
        return ResponseEntity.noContent().build();
    }
}
