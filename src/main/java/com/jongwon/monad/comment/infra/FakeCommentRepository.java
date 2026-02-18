package com.jongwon.monad.comment.infra;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("local")
public class FakeCommentRepository implements CommentRepository {

    private final Map<Long, Comment> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.assignId(sequence.incrementAndGet());
        }
        store.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Comment> findAllByPostId(Long postId) {
        return store.values().stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .toList();
    }

    @Override
    public long countByPostId(Long postId) {
        return store.values().stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .count();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public void deleteAllByParentId(Long parentId) {
        store.entrySet().removeIf(entry ->
                parentId.equals(entry.getValue().getParentId()));
    }

    @Override
    public List<Comment> findAllByMemberId(Long memberId, int page, int size) {
        return store.values().stream()
                .filter(comment -> comment.getMemberId().equals(memberId))
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    @Override
    public long countByMemberId(Long memberId) {
        return store.values().stream()
                .filter(comment -> comment.getMemberId().equals(memberId))
                .count();
    }
}
