package com.jongwon.monad.post.myposts;

import java.time.LocalDateTime;
import java.util.List;

public record MyPostsResponse(
        List<PostItem> posts,
        long totalCount,
        int page,
        int size
) {
    public record PostItem(
            Long id,
            Long boardId,
            String boardName,
            String title,
            int viewCount,
            LocalDateTime createdAt
    ) {}
}
