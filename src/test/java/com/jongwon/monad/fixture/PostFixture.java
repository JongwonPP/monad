package com.jongwon.monad.fixture;

import com.jongwon.monad.post.domain.Post;

public class PostFixture {

    public static Post create(Long boardId) {
        return Post.create(boardId, "테스트 제목", "테스트 본문", 1L);
    }

    public static Post createWithTitle(Long boardId, String title) {
        return Post.create(boardId, title, "본문 내용", 1L);
    }
}
