package com.jongwon.monad.post.getimage;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetImageController {

    private final GetImageUseCase getImageUseCase;

    public GetImageController(GetImageUseCase getImageUseCase) {
        this.getImageUseCase = getImageUseCase;
    }

    @GetMapping("/api/v1/images/{storedFilename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String storedFilename) {
        GetImageUseCase.ImageData imageData = getImageUseCase.execute(storedFilename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageData.contentType()))
                .body(imageData.data());
    }
}
