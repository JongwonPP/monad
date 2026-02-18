package com.jongwon.monad.post.searchposts;

import java.time.LocalDateTime;
import java.util.List;

public record SearchPostsResponse(
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
            Long memberId,
            String nickname,
            int viewCount,
            LocalDateTime createdAt
    ) {}
}
