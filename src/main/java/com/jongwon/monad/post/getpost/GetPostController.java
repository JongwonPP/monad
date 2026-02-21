package com.jongwon.monad.post.getpost;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post", description = "게시글 API")
@RestController
public class GetPostController {

    private final GetPostUseCase getPostUseCase;

    public GetPostController(GetPostUseCase getPostUseCase) {
        this.getPostUseCase = getPostUseCase;
    }

    @Operation(summary = "게시글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @GetMapping("/api/v1/boards/{boardId}/posts/{postId}")
    public ResponseEntity<GetPostResponse> getPost(
            @PathVariable Long boardId,
            @PathVariable Long postId) {
        Long currentMemberId = extractCurrentMemberId();
        GetPostResponse response = getPostUseCase.execute(postId, currentMemberId);
        return ResponseEntity.ok(response);
    }

    private Long extractCurrentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof com.jongwon.monad.global.security.AuthenticationPrincipal principal) {
            return principal.memberId();
        }
        return null;
    }
}
