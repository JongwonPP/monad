package com.jongwon.monad.post.getpost;

import java.time.LocalDateTime;
import java.util.List;

public record GetPostResponse(
        Long id,
        Long boardId,
        String title,
        String content,
        Long memberId,
        String nickname,
        int viewCount,
        long likeCount,
        boolean liked,
        LocalDateTime createdAt,
        List<ImageItem> images
) {}
