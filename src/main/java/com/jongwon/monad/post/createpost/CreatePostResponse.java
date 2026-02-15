package com.jongwon.monad.post.createpost;

import java.time.LocalDateTime;

public record CreatePostResponse(
        Long id,
        Long boardId,
        String title,
        String content,
        String author,
        LocalDateTime createdAt
) {}
