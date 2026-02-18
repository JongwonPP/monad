package com.jongwon.monad.comment.mycomments;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyCommentsController {

    private final MyCommentsUseCase myCommentsUseCase;

    public MyCommentsController(MyCommentsUseCase myCommentsUseCase) {
        this.myCommentsUseCase = myCommentsUseCase;
    }

    @GetMapping("/api/v1/members/me/comments")
    public ResponseEntity<MyCommentsResponse> myComments(
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        MyCommentsResponse response = myCommentsUseCase.execute(principal.memberId(), page, size);
        return ResponseEntity.ok(response);
    }
}
