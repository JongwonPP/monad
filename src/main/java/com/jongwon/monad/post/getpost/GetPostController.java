package com.jongwon.monad.post.getpost;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetPostController {

    private final GetPostUseCase getPostUseCase;

    public GetPostController(GetPostUseCase getPostUseCase) {
        this.getPostUseCase = getPostUseCase;
    }

    @GetMapping("/api/v1/boards/{boardId}/posts/{postId}")
    public ResponseEntity<GetPostResponse> getPost(
            @PathVariable Long boardId,
            @PathVariable Long postId) {
        GetPostResponse response = getPostUseCase.execute(postId);
        return ResponseEntity.ok(response);
    }
}
