package com.jongwon.monad.comment.createcomment;

import java.time.LocalDateTime;
import java.util.List;

public record CreateCommentResponse(
        Long id,
        Long postId,
        String author,
        String content,
        List<String> mentions,
        LocalDateTime createdAt
) {}
