package com.jongwon.monad.post.infra;

import com.jongwon.monad.post.domain.PostLike;
import com.jongwon.monad.post.domain.PostLikeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("local")
public class FakePostLikeRepository implements PostLikeRepository {

    private final Map<Long, PostLike> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public PostLike save(PostLike postLike) {
        if (postLike.getId() == null) {
            postLike.assignId(sequence.incrementAndGet());
        }
        store.put(postLike.getId(), postLike);
        return postLike;
    }

    @Override
    public Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId) {
        return store.values().stream()
                .filter(like -> like.getPostId().equals(postId) && like.getMemberId().equals(memberId))
                .findFirst();
    }

    @Override
    public long countByPostId(Long postId) {
        return store.values().stream()
                .filter(like -> like.getPostId().equals(postId))
                .count();
    }

    @Override
    public void deleteByPostIdAndMemberId(Long postId, Long memberId) {
        store.entrySet().removeIf(entry ->
                entry.getValue().getPostId().equals(postId)
                        && entry.getValue().getMemberId().equals(memberId));
    }
}
