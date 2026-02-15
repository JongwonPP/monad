package com.jongwon.monad.post.getpost;

import java.time.LocalDateTime;

public record GetPostResponse(
        Long id,
        Long boardId,
        String title,
        String content,
        String author,
        int viewCount,
        LocalDateTime createdAt
) {}
