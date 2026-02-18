package com.jongwon.monad.comment.infra;

import com.jongwon.monad.comment.domain.CommentLike;
import com.jongwon.monad.comment.domain.CommentLikeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("local")
public class FakeCommentLikeRepository implements CommentLikeRepository {

    private final Map<Long, CommentLike> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public CommentLike save(CommentLike commentLike) {
        if (commentLike.getId() == null) {
            commentLike.assignId(sequence.incrementAndGet());
        }
        store.put(commentLike.getId(), commentLike);
        return commentLike;
    }

    @Override
    public Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId) {
        return store.values().stream()
                .filter(like -> like.getCommentId().equals(commentId) && like.getMemberId().equals(memberId))
                .findFirst();
    }

    @Override
    public long countByCommentId(Long commentId) {
        return store.values().stream()
                .filter(like -> like.getCommentId().equals(commentId))
                .count();
    }

    @Override
    public void deleteByCommentIdAndMemberId(Long commentId, Long memberId) {
        store.entrySet().removeIf(entry ->
                entry.getValue().getCommentId().equals(commentId)
                        && entry.getValue().getMemberId().equals(memberId));
    }
}
