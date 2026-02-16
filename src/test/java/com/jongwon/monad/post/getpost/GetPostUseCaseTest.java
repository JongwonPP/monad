package com.jongwon.monad.post.getpost;

import com.jongwon.monad.fixture.MemberFixture;
import com.jongwon.monad.fixture.PostFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.infra.FakeMemberRepository;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.infra.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetPostUseCaseTest {

    private GetPostUseCase useCase;
    private PostRepository postRepository;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        memberRepository = new FakeMemberRepository();
        useCase = new GetPostUseCase(postRepository, memberRepository);
    }

    @Test
    void 게시글_조회_성공_및_조회수_증가() {
        Member member = MemberFixture.create();
        memberRepository.save(member);

        Post post = PostFixture.create(1L);
        postRepository.save(post);

        GetPostResponse response = useCase.execute(post.getId());

        assertThat(response.id()).isEqualTo(post.getId());
        assertThat(response.title()).isEqualTo("테스트 제목");
        assertThat(response.viewCount()).isEqualTo(1);
    }

    @Test
    void 존재하지_않는_게시글_조회시_예외() {
        assertThatThrownBy(() -> useCase.execute(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
