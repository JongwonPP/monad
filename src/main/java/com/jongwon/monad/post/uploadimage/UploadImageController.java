package com.jongwon.monad.post.uploadimage;

import com.jongwon.monad.global.security.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadImageController {

    private final UploadImageUseCase uploadImageUseCase;

    public UploadImageController(UploadImageUseCase uploadImageUseCase) {
        this.uploadImageUseCase = uploadImageUseCase;
    }

    @PostMapping("/api/v1/posts/{postId}/images")
    public ResponseEntity<UploadImageResponse> uploadImage(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthenticationPrincipal principal) {
        try {
            UploadImageResponse response = uploadImageUseCase.execute(
                    postId,
                    principal.memberId(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다");
        }
    }
}
