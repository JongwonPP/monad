package com.jongwon.monad.comment.fake;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;

import java.util.*;

public class FakeCommentRepository implements CommentRepository {

    private final Map<Long, Comment> store = new HashMap<>();
    private long sequence = 0L;

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.assignId(++sequence);
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
}
