package com.jongwon.monad.post.deletepost;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post", description = "게시글 API")
@RestController
public class DeletePostController {

    private final DeletePostUseCase deletePostUseCase;

    public DeletePostController(DeletePostUseCase deletePostUseCase) {
        this.deletePostUseCase = deletePostUseCase;
    }

    @Operation(summary = "게시글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공"),
            @ApiResponse(responseCode = "403", description = "본인 글만"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @DeleteMapping("/api/v1/boards/{boardId}/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        deletePostUseCase.execute(postId, principal.memberId());
        return ResponseEntity.noContent().build();
    }
}
