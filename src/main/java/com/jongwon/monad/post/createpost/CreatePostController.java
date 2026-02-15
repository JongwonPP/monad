package com.jongwon.monad.post.createpost;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreatePostController {

    private final CreatePostUseCase createPostUseCase;

    public CreatePostController(CreatePostUseCase createPostUseCase) {
        this.createPostUseCase = createPostUseCase;
    }

    @PostMapping("/api/v1/boards/{boardId}/posts")
    public ResponseEntity<CreatePostResponse> createPost(
            @PathVariable Long boardId,
            @Valid @RequestBody CreatePostRequest request) {
        CreatePostResponse response = createPostUseCase.execute(boardId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
