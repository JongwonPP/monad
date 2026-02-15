package com.jongwon.monad.post.updatepost;

import java.time.LocalDateTime;

public record UpdatePostResponse(
        Long id,
        String title,
        String content,
        LocalDateTime updatedAt
) {}
