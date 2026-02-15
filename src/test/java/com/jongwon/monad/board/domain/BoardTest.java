package com.jongwon.monad.board.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardTest {

    @Test
    void 정상_생성() {
        Board board = Board.create("자유게시판", "자유롭게 글을 작성하세요");

        assertThat(board.getName()).isEqualTo("자유게시판");
        assertThat(board.getDescription()).isEqualTo("자유롭게 글을 작성하세요");
        assertThat(board.getCreatedAt()).isNotNull();
        assertThat(board.getUpdatedAt()).isNotNull();
    }

    @Test
    void name_빈문자열이면_예외() {
        assertThatThrownBy(() -> Board.create("", "설명"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void name_null이면_예외() {
        assertThatThrownBy(() -> Board.create(null, "설명"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void name_50자_초과면_예외() {
        String longName = "a".repeat(51);
        assertThatThrownBy(() -> Board.create(longName, "설명"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 정상_수정() {
        Board board = Board.create("자유게시판", "설명");
        LocalDateTimeSnapshot before = new LocalDateTimeSnapshot(board.getUpdatedAt());

        board.update("공지게시판", "공지사항을 올리는 게시판");

        assertThat(board.getName()).isEqualTo("공지게시판");
        assertThat(board.getDescription()).isEqualTo("공지사항을 올리는 게시판");
        assertThat(board.getUpdatedAt()).isAfterOrEqualTo(before.value());
    }

    private record LocalDateTimeSnapshot(java.time.LocalDateTime value) {}
}
