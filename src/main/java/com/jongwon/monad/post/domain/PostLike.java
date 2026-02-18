package com.jongwon.monad.post.domain;

import java.time.LocalDateTime;

public class PostLike {

    private Long id;
    private Long postId;
    private Long memberId;
    private LocalDateTime createdAt;

    private PostLike(Long postId, Long memberId, LocalDateTime createdAt) {
        this.postId = postId;
        this.memberId = memberId;
        this.createdAt = createdAt;
    }

    public static PostLike create(Long postId, Long memberId) {
        if (postId == null) {
            throw new IllegalArgumentException("게시글 ID는 필수입니다");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다");
        }
        return new PostLike(postId, memberId, LocalDateTime.now());
    }

    public static PostLike reconstruct(Long id, Long postId, Long memberId, LocalDateTime createdAt) {
        PostLike postLike = new PostLike(postId, memberId, createdAt);
        postLike.id = id;
        return postLike;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
