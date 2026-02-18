package com.jongwon.monad.fixture;

import com.jongwon.monad.comment.domain.CommentLike;

public class CommentLikeFixture {

    public static CommentLike create(Long commentId, Long memberId) {
        return CommentLike.create(commentId, memberId);
    }
}
