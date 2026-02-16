package com.jongwon.monad.comment.createcomment;

import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.comment.infra.FakeCommentRepository;
import com.jongwon.monad.fixture.MemberFixture;
import com.jongwon.monad.fixture.PostFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.infra.FakeMemberRepository;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.infra.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateCommentUseCaseTest {

    private CreateCommentUseCase useCase;
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        commentRepository = new FakeCommentRepository();
        postRepository = new FakePostRepository();
        memberRepository = new FakeMemberRepository();
        useCase = new CreateCommentUseCase(commentRepository, postRepository, memberRepository);
    }

    @Test
    void 댓글_작성_성공() {
        var post = PostFixture.create(1L);
        postRepository.save(post);

        CreateCommentRequest request = new CreateCommentRequest("댓글 내용입니다");
        CreateCommentResponse response = useCase.execute(post.getId(), 1L, request);

        assertThat(response.id()).isNotNull();
        assertThat(response.postId()).isEqualTo(post.getId());
        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.content()).isEqualTo("댓글 내용입니다");
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void 존재하지_않는_게시글에_댓글_작성시_예외() {
        CreateCommentRequest request = new CreateCommentRequest("댓글 내용");

        assertThatThrownBy(() -> useCase.execute(999L, 1L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }

    @Test
    void 멘션된_닉네임이_존재하면_mentions에_포함() {
        var post = PostFixture.create(1L);
        postRepository.save(post);

        var member = MemberFixture.createWithNickname("홍길동");
        memberRepository.save(member);

        CreateCommentRequest request = new CreateCommentRequest("@홍길동 안녕하세요");
        CreateCommentResponse response = useCase.execute(post.getId(), 1L, request);

        assertThat(response.mentions()).containsExactly("홍길동");
    }

    @Test
    void 멘션된_닉네임이_존재하지_않으면_mentions에서_제외() {
        var post = PostFixture.create(1L);
        postRepository.save(post);

        CreateCommentRequest request = new CreateCommentRequest("@없는유저 안녕하세요");
        CreateCommentResponse response = useCase.execute(post.getId(), 1L, request);

        assertThat(response.mentions()).isEmpty();
    }
}
