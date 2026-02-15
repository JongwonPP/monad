package com.jongwon.monad.comment.createreply;

import java.time.LocalDateTime;
import java.util.List;

public record CreateReplyResponse(
        Long id,
        Long postId,
        Long parentId,
        String author,
        String content,
        List<String> mentions,
        LocalDateTime createdAt
) {}
