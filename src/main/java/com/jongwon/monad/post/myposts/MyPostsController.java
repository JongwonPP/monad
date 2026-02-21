package com.jongwon.monad.post.myposts;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post", description = "게시글 API")
@RestController
public class MyPostsController {

    private final MyPostsUseCase myPostsUseCase;

    public MyPostsController(MyPostsUseCase myPostsUseCase) {
        this.myPostsUseCase = myPostsUseCase;
    }

    @Operation(summary = "내가 쓴 글 조회")
    @GetMapping("/api/v1/members/me/posts")
    public ResponseEntity<MyPostsResponse> myPosts(
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        MyPostsResponse response = myPostsUseCase.execute(principal.memberId(), page, size);
        return ResponseEntity.ok(response);
    }
}
