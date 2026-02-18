package com.jongwon.monad.post.myposts;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.board.infra.FakeBoardRepository;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.infra.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MyPostsUseCaseTest {

    private MyPostsUseCase useCase;
    private PostRepository postRepository;
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        boardRepository = new FakeBoardRepository();
        useCase = new MyPostsUseCase(postRepository, boardRepository);
    }

    @Test
    void 내가_쓴_글_조회_성공() {
        Long memberId = 1L;
        Long boardId = 1L;
        Board board = Board.create("자유게시판", "자유롭게 작성");
        boardRepository.save(board);

        postRepository.save(Post.create(boardId, "첫 번째 글", "내용1", memberId));
        postRepository.save(Post.create(boardId, "두 번째 글", "내용2", memberId));
        postRepository.save(Post.create(boardId, "세 번째 글", "내용3", memberId));

        MyPostsResponse response = useCase.execute(memberId, 0, 20);

        assertThat(response.posts()).hasSize(3);
        assertThat(response.totalCount()).isEqualTo(3);
        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(20);
    }

    @Test
    void 다른_회원의_글은_포함되지_않음() {
        Long myId = 1L;
        Long otherId = 2L;
        Long boardId = 1L;
        Board board = Board.create("자유게시판", "자유롭게 작성");
        boardRepository.save(board);

        postRepository.save(Post.create(boardId, "내 글", "내용", myId));
        postRepository.save(Post.create(boardId, "다른 사람 글", "내용", otherId));

        MyPostsResponse response = useCase.execute(myId, 0, 20);

        assertThat(response.posts()).hasSize(1);
        assertThat(response.totalCount()).isEqualTo(1);
        assertThat(response.posts().getFirst().title()).isEqualTo("내 글");
    }

    @Test
    void 빈_결과_빈_리스트_반환() {
        Long memberId = 1L;

        MyPostsResponse response = useCase.execute(memberId, 0, 20);

        assertThat(response.posts()).isEmpty();
        assertThat(response.totalCount()).isZero();
    }

    @Test
    void 페이징_동작_확인() {
        Long memberId = 1L;
        Long boardId = 1L;
        Board board = Board.create("자유게시판", "자유롭게 작성");
        boardRepository.save(board);

        for (int i = 1; i <= 5; i++) {
            postRepository.save(Post.create(boardId, "글 " + i, "내용 " + i, memberId));
        }

        MyPostsResponse response = useCase.execute(memberId, 0, 2);

        assertThat(response.posts()).hasSize(2);
        assertThat(response.totalCount()).isEqualTo(5);
    }

    @Test
    void boardName_포함_확인() {
        Long memberId = 1L;
        Board board = Board.create("질문게시판", "질문을 올리세요");
        boardRepository.save(board);
        Long boardId = board.getId();

        postRepository.save(Post.create(boardId, "질문입니다", "내용", memberId));

        MyPostsResponse response = useCase.execute(memberId, 0, 20);

        assertThat(response.posts().getFirst().boardName()).isEqualTo("질문게시판");
        assertThat(response.posts().getFirst().boardId()).isEqualTo(boardId);
    }

    @Test
    void 삭제된_게시판의_글은_boardName이_삭제된_게시판() {
        Long memberId = 1L;
        Long deletedBoardId = 999L;

        postRepository.save(Post.create(deletedBoardId, "고아 글", "내용", memberId));

        MyPostsResponse response = useCase.execute(memberId, 0, 20);

        assertThat(response.posts().getFirst().boardName()).isEqualTo("삭제된 게시판");
    }
}
