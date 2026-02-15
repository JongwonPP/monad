package com.jongwon.monad.fixture;

import com.jongwon.monad.board.domain.Board;

public class BoardFixture {

    public static Board create() {
        return Board.create("자유게시판", "자유롭게 글을 작성하세요");
    }

    public static Board createWithName(String name) {
        return Board.create(name, name + " 설명");
    }
}
