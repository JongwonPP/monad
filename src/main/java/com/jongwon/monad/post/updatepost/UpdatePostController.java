package com.jongwon.monad.post.updatepost;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post", description = "게시글 API")
@RestController
public class UpdatePostController {

    private final UpdatePostUseCase updatePostUseCase;

    public UpdatePostController(UpdatePostUseCase updatePostUseCase) {
        this.updatePostUseCase = updatePostUseCase;
    }

    @Operation(summary = "게시글 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "본인 글만"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PutMapping("/api/v1/boards/{boardId}/posts/{postId}")
    public ResponseEntity<UpdatePostResponse> updatePost(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        UpdatePostResponse response = updatePostUseCase.execute(postId, principal.memberId(), request);
        return ResponseEntity.ok(response);
    }
}
