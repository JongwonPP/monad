package com.jongwon.monad.post.infra;

import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.domain.PostSortType;
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
public class FakePostRepository implements PostRepository {

    private final Map<Long, Post> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            post.assignId(sequence.incrementAndGet());
        }
        store.put(post.getId(), post);
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Post> findAllByBoardId(Long boardId, int page, int size, PostSortType sortType) {
        return store.values().stream()
                .filter(post -> post.getBoardId().equals(boardId))
                .sorted(sortComparator(sortType))
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

    @Override
    public List<Post> searchByKeyword(String keyword, int page, int size) {
        return store.values().stream()
                .filter(post -> post.getTitle().contains(keyword) || post.getContent().contains(keyword))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    @Override
    public long countByKeyword(String keyword) {
        return store.values().stream()
                .filter(post -> post.getTitle().contains(keyword) || post.getContent().contains(keyword))
                .count();
    }

    @Override
    public List<Post> searchByBoardIdAndKeyword(Long boardId, String keyword, int page, int size) {
        return store.values().stream()
                .filter(post -> post.getBoardId().equals(boardId))
                .filter(post -> post.getTitle().contains(keyword) || post.getContent().contains(keyword))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    @Override
    public long countByBoardIdAndKeyword(Long boardId, String keyword) {
        return store.values().stream()
                .filter(post -> post.getBoardId().equals(boardId))
                .filter(post -> post.getTitle().contains(keyword) || post.getContent().contains(keyword))
                .count();
    }

    @Override
    public List<Post> findAllByMemberId(Long memberId, int page, int size) {
        return store.values().stream()
                .filter(post -> post.getMemberId().equals(memberId))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    @Override
    public long countByMemberId(Long memberId) {
        return store.values().stream()
                .filter(post -> post.getMemberId().equals(memberId))
                .count();
    }

    private Comparator<Post> sortComparator(PostSortType sortType) {
        return switch (sortType) {
            case LATEST -> Comparator.comparing(Post::getCreatedAt).reversed();
            case OLDEST -> Comparator.comparing(Post::getCreatedAt);
            case VIEWS -> Comparator.comparing(Post::getViewCount).reversed()
                    .thenComparing(Comparator.comparing(Post::getCreatedAt).reversed());
            case LIKES -> Comparator.comparing(Post::getCreatedAt).reversed();
        };
    }
}
