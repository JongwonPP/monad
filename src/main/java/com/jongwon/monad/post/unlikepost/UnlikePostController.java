package com.jongwon.monad.post.unlikepost;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post Like", description = "게시글 좋아요 API")
@RestController
public class UnlikePostController {

    private final UnlikePostUseCase unlikePostUseCase;

    public UnlikePostController(UnlikePostUseCase unlikePostUseCase) {
        this.unlikePostUseCase = unlikePostUseCase;
    }

    @Operation(summary = "게시글 좋아요 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "좋아요하지 않음"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @DeleteMapping("/api/v1/posts/{postId}/likes")
    public ResponseEntity<UnlikePostResponse> unlikePost(
            @PathVariable Long postId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        UnlikePostResponse response = unlikePostUseCase.execute(postId, principal.memberId());
        return ResponseEntity.ok(response);
    }
}
