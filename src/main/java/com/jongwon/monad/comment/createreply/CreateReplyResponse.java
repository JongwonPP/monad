package com.jongwon.monad.comment.createreply;

import java.time.LocalDateTime;
import java.util.List;

public record CreateReplyResponse(
        Long id,
        Long postId,
        Long parentId,
        Long memberId,
        String content,
        List<String> mentions,
        LocalDateTime createdAt
) {}
