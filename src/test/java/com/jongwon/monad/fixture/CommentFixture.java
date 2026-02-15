package com.jongwon.monad.fixture;

import com.jongwon.monad.comment.domain.Comment;

public class CommentFixture {

    public static Comment create(Long postId) {
        return Comment.create(postId, null, 1L, "테스트 댓글입니다");
    }

    public static Comment createReply(Long postId, Long parentId) {
        return Comment.create(postId, parentId, 2L, "테스트 답글입니다");
    }

    public static Comment createWithMention(Long postId, String nickname) {
        return Comment.create(postId, null, 1L, "@" + nickname + " 안녕하세요");
    }
}
