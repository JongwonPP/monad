package com.jongwon.monad.comment.updatecomment;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateCommentResponse(
        Long id,
        String content,
        List<String> mentions,
        LocalDateTime updatedAt
) {}
