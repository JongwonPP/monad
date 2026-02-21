package com.jongwon.monad.comment.listcomments;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment", description = "댓글 API")
@RestController
public class ListCommentsController {

    private final ListCommentsUseCase listCommentsUseCase;

    public ListCommentsController(ListCommentsUseCase listCommentsUseCase) {
        this.listCommentsUseCase = listCommentsUseCase;
    }

    @Operation(summary = "댓글 목록 조회")
    @GetMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<ListCommentsResponse> listComments(@PathVariable Long postId) {
        ListCommentsResponse response = listCommentsUseCase.execute(postId);
        return ResponseEntity.ok(response);
    }
}
