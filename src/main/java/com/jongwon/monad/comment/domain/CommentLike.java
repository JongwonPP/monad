package com.jongwon.monad.comment.domain;

import java.time.LocalDateTime;

public class CommentLike {

    private Long id;
    private Long commentId;
    private Long memberId;
    private LocalDateTime createdAt;

    private CommentLike(Long commentId, Long memberId, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.memberId = memberId;
        this.createdAt = createdAt;
    }

    public static CommentLike create(Long commentId, Long memberId) {
        if (commentId == null) {
            throw new IllegalArgumentException("댓글 ID는 필수입니다");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다");
        }
        return new CommentLike(commentId, memberId, LocalDateTime.now());
    }

    public static CommentLike reconstruct(Long id, Long commentId, Long memberId, LocalDateTime createdAt) {
        CommentLike commentLike = new CommentLike(commentId, memberId, createdAt);
        commentLike.id = id;
        return commentLike;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
