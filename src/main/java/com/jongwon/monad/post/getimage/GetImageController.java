package com.jongwon.monad.post.getimage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Image", description = "이미지 API")
@RestController
public class GetImageController {

    private final GetImageUseCase getImageUseCase;

    public GetImageController(GetImageUseCase getImageUseCase) {
        this.getImageUseCase = getImageUseCase;
    }

    @Operation(summary = "이미지 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "이미지 없음")
    })
    @GetMapping("/api/v1/images/{storedFilename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String storedFilename) {
        GetImageUseCase.ImageData imageData = getImageUseCase.execute(storedFilename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageData.contentType()))
                .body(imageData.data());
    }
}
