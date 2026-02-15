package com.jongwon.monad.comment.listcomments;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListCommentsController {

    private final ListCommentsUseCase listCommentsUseCase;

    public ListCommentsController(ListCommentsUseCase listCommentsUseCase) {
        this.listCommentsUseCase = listCommentsUseCase;
    }

    @GetMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<ListCommentsResponse> listComments(@PathVariable Long postId) {
        ListCommentsResponse response = listCommentsUseCase.execute(postId);
        return ResponseEntity.ok(response);
    }
}
