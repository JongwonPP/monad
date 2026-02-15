package com.jongwon.monad.comment.createreply;

import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.comment.fake.FakeCommentRepository;
import com.jongwon.monad.fixture.CommentFixture;
import com.jongwon.monad.fixture.PostFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.fake.FakeMemberRepository;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.fake.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateReplyUseCaseTest {

    private CreateReplyUseCase useCase;
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        commentRepository = new FakeCommentRepository();
        postRepository = new FakePostRepository();
        memberRepository = new FakeMemberRepository();
        useCase = new CreateReplyUseCase(commentRepository, postRepository, memberRepository);
    }

    @Test
    void 대댓글_작성_성공() {
        var post = PostFixture.create(1L);
        postRepository.save(post);

        var parent = CommentFixture.create(post.getId());
        commentRepository.save(parent);

        CreateReplyRequest request = new CreateReplyRequest("답글작성자", "답글 내용입니다");
        CreateReplyResponse response = useCase.execute(post.getId(), parent.getId(), request);

        assertThat(response.id()).isNotNull();
        assertThat(response.postId()).isEqualTo(post.getId());
        assertThat(response.parentId()).isEqualTo(parent.getId());
        assertThat(response.author()).isEqualTo("답글작성자");
        assertThat(response.content()).isEqualTo("답글 내용입니다");
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void 존재하지_않는_부모_댓글에_대댓글_작성시_예외() {
        var post = PostFixture.create(1L);
        postRepository.save(post);

        CreateReplyRequest request = new CreateReplyRequest("답글작성자", "답글 내용");

        assertThatThrownBy(() -> useCase.execute(post.getId(), 999L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("댓글을 찾을 수 없습니다");
    }

    @Test
    void 대댓글에_대한_대댓글_시도시_예외() {
        var post = PostFixture.create(1L);
        postRepository.save(post);

        var parent = CommentFixture.create(post.getId());
        commentRepository.save(parent);

        var reply = CommentFixture.createReply(post.getId(), parent.getId());
        commentRepository.save(reply);

        CreateReplyRequest request = new CreateReplyRequest("답글작성자", "대대댓글 시도");

        assertThatThrownBy(() -> useCase.execute(post.getId(), reply.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("대댓글에는 답글을 달 수 없습니다");
    }
}
