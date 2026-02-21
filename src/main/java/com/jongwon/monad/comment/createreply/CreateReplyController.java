package com.jongwon.monad.comment.createreply;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment", description = "댓글 API")
@RestController
public class CreateReplyController {

    private final CreateReplyUseCase createReplyUseCase;

    public CreateReplyController(CreateReplyUseCase createReplyUseCase) {
        this.createReplyUseCase = createReplyUseCase;
    }

    @Operation(summary = "대댓글 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "400", description = "대대댓글 불가"),
            @ApiResponse(responseCode = "404", description = "게시글/댓글 없음")
    })
    @PostMapping("/api/v1/posts/{postId}/comments/{commentId}/replies")
    public ResponseEntity<CreateReplyResponse> createReply(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal,
            @Valid @RequestBody CreateReplyRequest request) {
        CreateReplyResponse response = createReplyUseCase.execute(postId, commentId, principal.memberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
