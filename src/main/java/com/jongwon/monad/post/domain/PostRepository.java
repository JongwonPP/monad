package com.jongwon.monad.post.domain;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post save(Post post);

    Optional<Post> findById(Long id);

    List<Post> findAllByBoardId(Long boardId, int page, int size);

    long countByBoardId(Long boardId);

    void deleteById(Long id);
}
