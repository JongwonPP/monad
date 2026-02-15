package com.jongwon.monad.post.updatepost;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdatePostController {

    private final UpdatePostUseCase updatePostUseCase;

    public UpdatePostController(UpdatePostUseCase updatePostUseCase) {
        this.updatePostUseCase = updatePostUseCase;
    }

    @PutMapping("/api/v1/boards/{boardId}/posts/{postId}")
    public ResponseEntity<UpdatePostResponse> updatePost(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request) {
        UpdatePostResponse response = updatePostUseCase.execute(postId, request);
        return ResponseEntity.ok(response);
    }
}
