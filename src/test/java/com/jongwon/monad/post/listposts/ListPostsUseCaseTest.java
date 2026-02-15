package com.jongwon.monad.post.listposts;

import com.jongwon.monad.fixture.PostFixture;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.fake.FakeMemberRepository;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.fake.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ListPostsUseCaseTest {

    private ListPostsUseCase useCase;
    private PostRepository postRepository;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        memberRepository = new FakeMemberRepository();
        useCase = new ListPostsUseCase(postRepository, memberRepository);
    }

    @Test
    void 게시글_목록_조회() {
        Long boardId = 1L;
        postRepository.save(PostFixture.createWithTitle(boardId, "첫 번째 글"));
        postRepository.save(PostFixture.createWithTitle(boardId, "두 번째 글"));
        postRepository.save(PostFixture.createWithTitle(boardId, "세 번째 글"));

        ListPostsResponse response = useCase.execute(boardId, 0, 20);

        assertThat(response.posts()).hasSize(3);
        assertThat(response.totalCount()).isEqualTo(3);
        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(20);
    }

    @Test
    void 페이징_조회() {
        Long boardId = 1L;
        for (int i = 1; i <= 5; i++) {
            postRepository.save(PostFixture.createWithTitle(boardId, "글 " + i));
        }

        ListPostsResponse response = useCase.execute(boardId, 0, 2);

        assertThat(response.posts()).hasSize(2);
        assertThat(response.totalCount()).isEqualTo(5);
    }
}
