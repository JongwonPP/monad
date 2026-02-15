package com.jongwon.monad.comment.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentTest {

    @Test
    void 일반_댓글_정상_생성() {
        Comment comment = Comment.create(1L, null, "작성자", "댓글 내용");

        assertThat(comment.getPostId()).isEqualTo(1L);
        assertThat(comment.getParentId()).isNull();
        assertThat(comment.getAuthor()).isEqualTo("작성자");
        assertThat(comment.getContent()).isEqualTo("댓글 내용");
        assertThat(comment.isReply()).isFalse();
        assertThat(comment.getCreatedAt()).isNotNull();
        assertThat(comment.getUpdatedAt()).isNotNull();
    }

    @Test
    void 대댓글_정상_생성() {
        Comment reply = Comment.create(1L, 10L, "답글자", "대댓글 내용");

        assertThat(reply.getParentId()).isEqualTo(10L);
        assertThat(reply.isReply()).isTrue();
    }

    @Test
    void content_null이면_예외() {
        assertThatThrownBy(() -> Comment.create(1L, null, "작성자", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content_빈문자열이면_예외() {
        assertThatThrownBy(() -> Comment.create(1L, null, "작성자", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content_500자_초과면_예외() {
        String longContent = "a".repeat(501);
        assertThatThrownBy(() -> Comment.create(1L, null, "작성자", longContent))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void author_null이면_예외() {
        assertThatThrownBy(() -> Comment.create(1L, null, null, "내용"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void author_빈문자열이면_예외() {
        assertThatThrownBy(() -> Comment.create(1L, null, "", "내용"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void postId_null이면_예외() {
        assertThatThrownBy(() -> Comment.create(null, null, "작성자", "내용"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 댓글_수정_성공() {
        Comment comment = Comment.create(1L, null, "작성자", "원래 내용");
        LocalDateTimeSnapshot before = new LocalDateTimeSnapshot(comment.getUpdatedAt());

        comment.update("수정된 내용");

        assertThat(comment.getContent()).isEqualTo("수정된 내용");
        assertThat(comment.getUpdatedAt()).isAfterOrEqualTo(before.value());
    }

    @Test
    void isReply_parentId_null이면_false_있으면_true() {
        Comment comment = Comment.create(1L, null, "작성자", "내용");
        Comment reply = Comment.create(1L, 5L, "작성자", "내용");

        assertThat(comment.isReply()).isFalse();
        assertThat(reply.isReply()).isTrue();
    }

    @Test
    void 멘션_파싱_닉네임_추출() {
        Comment comment = Comment.create(1L, null, "작성자", "@홍길동 안녕");

        assertThat(comment.getMentions()).containsExactly("홍길동");
    }

    @Test
    void 멘션_파싱_멘션_없으면_빈리스트() {
        Comment comment = Comment.create(1L, null, "작성자", "멘션 없는 댓글");

        assertThat(comment.getMentions()).isEmpty();
    }

    @Test
    void 멘션_파싱_중복_제거() {
        Comment comment = Comment.create(1L, null, "작성자", "@홍길동 @홍길동 중복");

        assertThat(comment.getMentions()).containsExactly("홍길동");
    }

    @Test
    void 멘션_파싱_2자_미만_무시() {
        Comment comment = Comment.create(1L, null, "작성자", "@a 한글자");

        assertThat(comment.getMentions()).isEmpty();
    }

    @Test
    void 수정_시_멘션_재파싱() {
        Comment comment = Comment.create(1L, null, "작성자", "@홍길동 안녕");
        assertThat(comment.getMentions()).containsExactly("홍길동");

        comment.update("@김철수 반가워");

        assertThat(comment.getMentions()).containsExactly("김철수");
    }

    @Test
    void filterMentions_유효한_닉네임만_남김() {
        Comment comment = Comment.create(1L, null, "작성자", "@홍길동 @김철수 @이영희 안녕");
        assertThat(comment.getMentions()).containsExactly("홍길동", "김철수", "이영희");

        comment.filterMentions(List.of("홍길동", "이영희"));

        assertThat(comment.getMentions()).containsExactly("홍길동", "이영희");
    }

    private record LocalDateTimeSnapshot(java.time.LocalDateTime value) {}
}
