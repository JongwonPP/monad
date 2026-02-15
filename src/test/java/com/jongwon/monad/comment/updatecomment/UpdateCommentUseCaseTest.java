package com.jongwon.monad.comment.updatecomment;

import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.comment.fake.FakeCommentRepository;
import com.jongwon.monad.fixture.CommentFixture;
import com.jongwon.monad.fixture.MemberFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.fake.FakeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateCommentUseCaseTest {

    private UpdateCommentUseCase useCase;
    private CommentRepository commentRepository;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        commentRepository = new FakeCommentRepository();
        memberRepository = new FakeMemberRepository();
        useCase = new UpdateCommentUseCase(commentRepository, memberRepository);
    }

    @Test
    void 댓글_수정_성공_멘션_재검증() {
        var comment = CommentFixture.create(1L);
        commentRepository.save(comment);

        var member = MemberFixture.createWithNickname("홍길동");
        memberRepository.save(member);

        UpdateCommentRequest request = new UpdateCommentRequest("수정된 내용 @홍길동 확인");
        UpdateCommentResponse response = useCase.execute(comment.getId(), request);

        assertThat(response.id()).isEqualTo(comment.getId());
        assertThat(response.content()).isEqualTo("수정된 내용 @홍길동 확인");
        assertThat(response.mentions()).containsExactly("홍길동");
        assertThat(response.updatedAt()).isNotNull();
    }

    @Test
    void 존재하지_않는_댓글_수정시_예외() {
        UpdateCommentRequest request = new UpdateCommentRequest("수정된 내용");

        assertThatThrownBy(() -> useCase.execute(999L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("댓글을 찾을 수 없습니다");
    }
}
