package com.jongwon.monad.post.uploadimage;

import java.time.LocalDateTime;

public record UploadImageResponse(
        Long id,
        Long postId,
        String originalFilename,
        String storedFilename,
        String imageUrl,
        long fileSize,
        String contentType,
        LocalDateTime createdAt
) {}
