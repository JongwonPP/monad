package com.jongwon.monad.post.domain;

import java.util.List;
import java.util.Optional;

public interface PostImageRepository {

    PostImage save(PostImage postImage);

    Optional<PostImage> findById(Long id);

    Optional<PostImage> findByStoredFilename(String storedFilename);

    List<PostImage> findAllByPostId(Long postId);

    int countByPostId(Long postId);

    void deleteById(Long id);

    void deleteAllByPostId(Long postId);
}
