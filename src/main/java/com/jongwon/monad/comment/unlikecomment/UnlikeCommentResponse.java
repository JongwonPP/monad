package com.jongwon.monad.comment.unlikecomment;

public record UnlikeCommentResponse(
        Long commentId,
        boolean liked,
        long likeCount
) {}
