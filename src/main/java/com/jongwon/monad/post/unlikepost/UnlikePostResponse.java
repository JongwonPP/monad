package com.jongwon.monad.post.unlikepost;

public record UnlikePostResponse(
        Long postId,
        boolean liked,
        long likeCount
) {}
