package com.jongwon.monad.post.likepost;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post Like", description = "게시글 좋아요 API")
@RestController
public class LikePostController {

    private final LikePostUseCase likePostUseCase;

    public LikePostController(LikePostUseCase likePostUseCase) {
        this.likePostUseCase = likePostUseCase;
    }

    @Operation(summary = "게시글 좋아요")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "400", description = "이미 좋아요"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PostMapping("/api/v1/posts/{postId}/likes")
    public ResponseEntity<LikePostResponse> likePost(
            @PathVariable Long postId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        LikePostResponse response = likePostUseCase.execute(postId, principal.memberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
