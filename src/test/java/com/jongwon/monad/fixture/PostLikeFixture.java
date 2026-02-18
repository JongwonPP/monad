package com.jongwon.monad.fixture;

import com.jongwon.monad.post.domain.PostLike;

public class PostLikeFixture {

    public static PostLike create(Long postId, Long memberId) {
        return PostLike.create(postId, memberId);
    }
}
