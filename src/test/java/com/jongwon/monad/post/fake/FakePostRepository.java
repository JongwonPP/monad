package com.jongwon.monad.post.fake;

import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;

import java.util.*;

public class FakePostRepository implements PostRepository {

    private final Map<Long, Post> store = new HashMap<>();
    private long sequence = 0L;

    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            post.assignId(++sequence);
        }
        store.put(post.getId(), post);
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Post> findAllByBoardId(Long boardId, int page, int size) {
        return store.values().stream()
                .filter(post -> post.getBoardId().equals(boardId))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    @Override
    public long countByBoardId(Long boardId) {
        return store.values().stream()
                .filter(post -> post.getBoardId().equals(boardId))
                .count();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
