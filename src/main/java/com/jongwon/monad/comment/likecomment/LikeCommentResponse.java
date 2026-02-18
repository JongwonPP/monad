package com.jongwon.monad.comment.likecomment;

public record LikeCommentResponse(
        Long commentId,
        boolean liked,
        long likeCount
) {}
