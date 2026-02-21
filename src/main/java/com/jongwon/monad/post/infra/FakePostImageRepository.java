package com.jongwon.monad.post.infra;

import com.jongwon.monad.post.domain.PostImage;
import com.jongwon.monad.post.domain.PostImageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("local")
public class FakePostImageRepository implements PostImageRepository {

    private final Map<Long, PostImage> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public PostImage save(PostImage postImage) {
        if (postImage.getId() == null) {
            postImage.assignId(sequence.incrementAndGet());
        }
        store.put(postImage.getId(), postImage);
        return postImage;
    }

    @Override
    public Optional<PostImage> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<PostImage> findByStoredFilename(String storedFilename) {
        return store.values().stream()
                .filter(image -> image.getStoredFilename().equals(storedFilename))
                .findFirst();
    }

    @Override
    public List<PostImage> findAllByPostId(Long postId) {
        return store.values().stream()
                .filter(image -> image.getPostId().equals(postId))
                .toList();
    }

    @Override
    public int countByPostId(Long postId) {
        return (int) store.values().stream()
                .filter(image -> image.getPostId().equals(postId))
                .count();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public void deleteAllByPostId(Long postId) {
        store.entrySet().removeIf(entry -> entry.getValue().getPostId().equals(postId));
    }
}
