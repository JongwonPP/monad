package com.jongwon.monad.comment.domain;

import java.util.Optional;

public interface CommentLikeRepository {

    CommentLike save(CommentLike commentLike);

    Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId);

    long countByCommentId(Long commentId);

    void deleteByCommentIdAndMemberId(Long commentId, Long memberId);
}
