package com.jongwon.monad.post.listposts;

import java.time.LocalDateTime;
import java.util.List;

public record ListPostsResponse(
        List<PostItem> posts,
        long totalCount,
        int page,
        int size
) {
    public record PostItem(
            Long id,
            String title,
            Long memberId,
            String nickname,
            int viewCount,
            LocalDateTime createdAt
    ) {}
}
