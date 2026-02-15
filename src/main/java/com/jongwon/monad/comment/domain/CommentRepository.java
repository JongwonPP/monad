package com.jongwon.monad.comment.domain;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Comment save(Comment comment);

    Optional<Comment> findById(Long id);

    List<Comment> findAllByPostId(Long postId);

    long countByPostId(Long postId);

    void deleteById(Long id);

    void deleteAllByParentId(Long parentId);
}
