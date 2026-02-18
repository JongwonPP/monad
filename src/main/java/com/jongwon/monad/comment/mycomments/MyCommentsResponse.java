package com.jongwon.monad.comment.mycomments;

import java.time.LocalDateTime;
import java.util.List;

public record MyCommentsResponse(
        List<CommentItem> comments,
        long totalCount,
        int page,
        int size
) {
    public record CommentItem(
            Long id,
            Long postId,
            String postTitle,
            String content,
            List<String> mentions,
            LocalDateTime createdAt
    ) {}
}
