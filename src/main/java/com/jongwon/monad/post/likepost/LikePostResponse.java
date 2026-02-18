package com.jongwon.monad.post.likepost;

public record LikePostResponse(
        Long postId,
        boolean liked,
        long likeCount
) {}
