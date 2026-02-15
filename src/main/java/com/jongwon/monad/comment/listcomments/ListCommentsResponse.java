package com.jongwon.monad.comment.listcomments;

import java.time.LocalDateTime;
import java.util.List;

public record ListCommentsResponse(
        List<CommentItem> comments,
        long totalCount
) {
    public record CommentItem(
            Long id,
            String author,
            String content,
            List<String> mentions,
            LocalDateTime createdAt,
            List<ReplyItem> replies
    ) {}

    public record ReplyItem(
            Long id,
            String author,
            String content,
            List<String> mentions,
            LocalDateTime createdAt
    ) {}
}
