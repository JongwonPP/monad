package com.jongwon.monad.post.domain;

import java.util.Optional;

public interface PostLikeRepository {

    PostLike save(PostLike postLike);

    Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);

    long countByPostId(Long postId);

    void deleteByPostIdAndMemberId(Long postId, Long memberId);
}
