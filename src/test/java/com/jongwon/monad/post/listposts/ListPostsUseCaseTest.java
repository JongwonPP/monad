package com.jongwon.monad.post.listposts;

import com.jongwon.monad.fixture.PostFixture;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.infra.FakeMemberRepository;
import com.jongwon.monad.post.domain.PostLikeRepository;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.domain.PostSortType;
import com.jongwon.monad.post.infra.FakePostLikeRepository;
import com.jongwon.monad.post.infra.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jongwon.monad.post.domain.Post;

import static org.assertj.core.api.Assertions.assertThat;

class ListPostsUseCaseTest {

    private ListPostsUseCase useCase;
    private PostRepository postRepository;
    private MemberRepository memberRepository;
    private PostLikeRepository postLikeRepository;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        memberRepository = new FakeMemberRepository();
        postLikeRepository = new FakePostLikeRepository();
        useCase = new ListPostsUseCase(postRepository, memberRepository, postLikeRepository);
    }

    @Test
    void 게시글_목록_조회() {
        Long boardId = 1L;
        postRepository.save(PostFixture.createWithTitle(boardId, "첫 번째 글"));
        postRepository.save(PostFixture.createWithTitle(boardId, "두 번째 글"));
        postRepository.save(PostFixture.createWithTitle(boardId, "세 번째 글"));

        ListPostsResponse response = useCase.execute(boardId, 0, 20, PostSortType.LATEST);

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

        ListPostsResponse response = useCase.execute(boardId, 0, 2, PostSortType.LATEST);

        assertThat(response.posts()).hasSize(2);
        assertThat(response.totalCount()).isEqualTo(5);
    }

    @Test
    void 조회수순_정렬() {
        Long boardId = 1L;
        Post post1 = postRepository.save(PostFixture.createWithTitle(boardId, "조회수 낮은 글"));
        Post post2 = postRepository.save(PostFixture.createWithTitle(boardId, "조회수 중간 글"));
        Post post3 = postRepository.save(PostFixture.createWithTitle(boardId, "조회수 높은 글"));

        post1.increaseViewCount(); // 1회
        post2.increaseViewCount(); // 1회
        post2.increaseViewCount(); // 2회
        post3.increaseViewCount(); // 1회
        post3.increaseViewCount(); // 2회
        post3.increaseViewCount(); // 3회

        ListPostsResponse response = useCase.execute(boardId, 0, 20, PostSortType.VIEWS);

        assertThat(response.posts()).hasSize(3);
        assertThat(response.posts().get(0).viewCount()).isEqualTo(3);
        assertThat(response.posts().get(1).viewCount()).isEqualTo(2);
        assertThat(response.posts().get(2).viewCount()).isEqualTo(1);
    }

    @Test
    void 기본_정렬은_최신순() throws InterruptedException {
        Long boardId = 1L;
        postRepository.save(PostFixture.createWithTitle(boardId, "첫 번째 글"));
        Thread.sleep(10);
        postRepository.save(PostFixture.createWithTitle(boardId, "두 번째 글"));

        ListPostsResponse response = useCase.execute(boardId, 0, 20, PostSortType.LATEST);

        assertThat(response.posts()).hasSize(2);
        assertThat(response.posts().get(0).title()).isEqualTo("두 번째 글");
        assertThat(response.posts().get(1).title()).isEqualTo("첫 번째 글");
    }
}
