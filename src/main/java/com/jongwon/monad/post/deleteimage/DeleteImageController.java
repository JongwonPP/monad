package com.jongwon.monad.post.deleteimage;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Image", description = "이미지 API")
@RestController
public class DeleteImageController {

    private final DeleteImageUseCase deleteImageUseCase;

    public DeleteImageController(DeleteImageUseCase deleteImageUseCase) {
        this.deleteImageUseCase = deleteImageUseCase;
    }

    @Operation(summary = "이미지 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공"),
            @ApiResponse(responseCode = "403", description = "본인 글만"),
            @ApiResponse(responseCode = "404", description = "이미지 없음")
    })
    @DeleteMapping("/api/v1/posts/{postId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long postId,
            @PathVariable Long imageId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        deleteImageUseCase.execute(postId, imageId, principal.memberId());
        return ResponseEntity.noContent().build();
    }
}
