package com.jongwon.monad.comment.listcomments;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.comment.infra.FakeCommentRepository;
import com.jongwon.monad.fixture.CommentFixture;
import com.jongwon.monad.fixture.MemberFixture;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.infra.FakeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ListCommentsUseCaseTest {

    private ListCommentsUseCase useCase;
    private CommentRepository commentRepository;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        commentRepository = new FakeCommentRepository();
        memberRepository = new FakeMemberRepository();
        useCase = new ListCommentsUseCase(commentRepository, memberRepository);
    }

    @Test
    void 계층_구조_확인() {
        Long postId = 1L;

        Comment parent = CommentFixture.create(postId);
        commentRepository.save(parent);

        Comment reply = CommentFixture.createReply(postId, parent.getId());
        commentRepository.save(reply);

        ListCommentsResponse response = useCase.execute(postId);

        assertThat(response.totalCount()).isEqualTo(2);
        assertThat(response.comments()).hasSize(1);

        var commentItem = response.comments().getFirst();
        assertThat(commentItem.id()).isEqualTo(parent.getId());
        assertThat(commentItem.replies()).hasSize(1);
        assertThat(commentItem.replies().getFirst().id()).isEqualTo(reply.getId());
    }

    @Test
    void mentions_포함_확인() {
        Long postId = 1L;

        Comment comment = CommentFixture.createWithMention(postId, "홍길동");
        commentRepository.save(comment);

        ListCommentsResponse response = useCase.execute(postId);

        assertThat(response.comments()).hasSize(1);
        assertThat(response.comments().getFirst().mentions()).contains("홍길동");
    }
}
