package com.jongwon.monad.fixture;

import com.jongwon.monad.post.domain.PostImage;

public class PostImageFixture {

    public static PostImage create(Long postId) {
        return PostImage.create(postId, "test-image.jpg", "stored-uuid.jpg", "image/jpeg", 1024L);
    }

    public static PostImage createWithFilename(Long postId, String originalFilename) {
        return PostImage.create(postId, originalFilename, "stored-uuid.jpg", "image/jpeg", 1024L);
    }

    public static PostImage createPng(Long postId) {
        return PostImage.create(postId, "test-image.png", "stored-uuid.png", "image/png", 2048L);
    }
}
