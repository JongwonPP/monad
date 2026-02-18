package com.jongwon.monad.post.searchposts;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.board.infra.FakeBoardRepository;
import com.jongwon.monad.fixture.BoardFixture;
import com.jongwon.monad.fixture.MemberFixture;
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

class SearchPostsUseCaseTest {

    private SearchPostsUseCase useCase;
    private PostRepository postRepository;
    private MemberRepository memberRepository;
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        memberRepository = new FakeMemberRepository();
        boardRepository = new FakeBoardRepository();
        useCase = new SearchPostsUseCase(postRepository, memberRepository, boardRepository);
    }

    @Test
    void 제목_검색_성공() {
        Board board = boardRepository.save(BoardFixture.create());
        Member member = memberRepository.save(MemberFixture.create());
        postRepository.save(Post.create(board.getId(), "Spring Boot 입문", "내용입니다", member.getId()));
        postRepository.save(Post.create(board.getId(), "Java 기초", "자바 내용", member.getId()));

        SearchPostsResponse response = useCase.execute("Spring", null, 0, 20);

        assertThat(response.posts()).hasSize(1);
        assertThat(response.posts().getFirst().title()).isEqualTo("Spring Boot 입문");
        assertThat(response.totalCount()).isEqualTo(1);
    }

    @Test
    void 본문_검색_성공() {
        Board board = boardRepository.save(BoardFixture.create());
        Member member = memberRepository.save(MemberFixture.create());
        postRepository.save(Post.create(board.getId(), "제목1", "Spring Boot 관련 내용", member.getId()));
        postRepository.save(Post.create(board.getId(), "제목2", "React 관련 내용", member.getId()));

        SearchPostsResponse response = useCase.execute("Spring", null, 0, 20);

        assertThat(response.posts()).hasSize(1);
        assertThat(response.posts().getFirst().title()).isEqualTo("제목1");
    }

    @Test
    void 게시판_범위_검색() {
        Board board1 = boardRepository.save(BoardFixture.createWithName("자유게시판"));
        Board board2 = boardRepository.save(BoardFixture.createWithName("질문게시판"));
        Member member = memberRepository.save(MemberFixture.create());
        postRepository.save(Post.create(board1.getId(), "Spring 질문", "내용", member.getId()));
        postRepository.save(Post.create(board2.getId(), "Spring 답변", "내용", member.getId()));

        SearchPostsResponse response = useCase.execute("Spring", board1.getId(), 0, 20);

        assertThat(response.posts()).hasSize(1);
        assertThat(response.posts().getFirst().title()).isEqualTo("Spring 질문");
        assertThat(response.posts().getFirst().boardName()).isEqualTo("자유게시판");
    }

    @Test
    void 전체_검색() {
        Board board1 = boardRepository.save(BoardFixture.createWithName("자유게시판"));
        Board board2 = boardRepository.save(BoardFixture.createWithName("질문게시판"));
        Member member = memberRepository.save(MemberFixture.create());
        postRepository.save(Post.create(board1.getId(), "Spring 입문", "내용", member.getId()));
        postRepository.save(Post.create(board2.getId(), "Spring 심화", "내용", member.getId()));

        SearchPostsResponse response = useCase.execute("Spring", null, 0, 20);

        assertThat(response.posts()).hasSize(2);
        assertThat(response.totalCount()).isEqualTo(2);
    }

    @Test
    void 검색_결과_없음() {
        Board board = boardRepository.save(BoardFixture.create());
        Member member = memberRepository.save(MemberFixture.create());
        postRepository.save(Post.create(board.getId(), "Java 기초", "자바 내용", member.getId()));

        SearchPostsResponse response = useCase.execute("Python", null, 0, 20);

        assertThat(response.posts()).isEmpty();
        assertThat(response.totalCount()).isZero();
    }

    @Test
    void 빈_키워드_예외() {
        assertThatThrownBy(() -> useCase.execute("", null, 0, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("검색 키워드는 필수입니다");

        assertThatThrownBy(() -> useCase.execute(null, null, 0, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("검색 키워드는 필수입니다");

        assertThatThrownBy(() -> useCase.execute("   ", null, 0, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("검색 키워드는 필수입니다");
    }

    @Test
    void 페이징_동작() {
        Board board = boardRepository.save(BoardFixture.create());
        Member member = memberRepository.save(MemberFixture.create());
        for (int i = 1; i <= 5; i++) {
            postRepository.save(Post.create(board.getId(), "Spring 글 " + i, "내용", member.getId()));
        }

        SearchPostsResponse response = useCase.execute("Spring", null, 0, 2);

        assertThat(response.posts()).hasSize(2);
        assertThat(response.totalCount()).isEqualTo(5);
        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(2);
    }
}
