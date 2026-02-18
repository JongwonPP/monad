package com.jongwon.monad.post.getpost;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
