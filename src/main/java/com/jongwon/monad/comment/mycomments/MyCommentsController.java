package com.jongwon.monad.comment.mycomments;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment", description = "댓글 API")
@RestController
public class MyCommentsController {

    private final MyCommentsUseCase myCommentsUseCase;

    public MyCommentsController(MyCommentsUseCase myCommentsUseCase) {
        this.myCommentsUseCase = myCommentsUseCase;
    }

    @Operation(summary = "내가 쓴 댓글 조회")
    @GetMapping("/api/v1/members/me/comments")
    public ResponseEntity<MyCommentsResponse> myComments(
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        MyCommentsResponse response = myCommentsUseCase.execute(principal.memberId(), page, size);
        return ResponseEntity.ok(response);
    }
}
