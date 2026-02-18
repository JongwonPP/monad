package com.jongwon.monad.comment.mycomments;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.comment.infra.FakeCommentRepository;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.infra.FakePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MyCommentsUseCaseTest {

    private MyCommentsUseCase useCase;
    private CommentRepository commentRepository;
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        commentRepository = new FakeCommentRepository();
        postRepository = new FakePostRepository();
        useCase = new MyCommentsUseCase(commentRepository, postRepository);
    }

    @Test
    void 내가_쓴_댓글_조회_성공() {
        Long memberId = 1L;
        Post post = Post.create(1L, "테스트 게시글", "내용", 2L);
        postRepository.save(post);
        Long postId = post.getId();

        commentRepository.save(Comment.create(postId, null, memberId, "첫 번째 댓글"));
        commentRepository.save(Comment.create(postId, null, memberId, "두 번째 댓글"));

        MyCommentsResponse response = useCase.execute(memberId, 0, 20);

        assertThat(response.comments()).hasSize(2);
        assertThat(response.totalCount()).isEqualTo(2);
        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(20);
    }

    @Test
    void 댓글과_대댓글_모두_포함() {
        Long memberId = 1L;
        Post post = Post.create(1L, "테스트 게시글", "내용", 2L);
        postRepository.save(post);
        Long postId = post.getId();

        Comment parent = Comment.create(postId, null, memberId, "댓글");
        commentRepository.save(parent);
        commentRepository.save(Comment.create(postId, parent.getId(), memberId, "대댓글"));

        MyCommentsResponse response = useCase.execute(memberId, 0, 20);

        assertThat(response.comments()).hasSize(2);
    }

    @Test
    void 다른_회원의_댓글은_포함되지_않음() {
        Long myId = 1L;
        Long otherId = 2L;
        Post post = Post.create(1L, "테스트 게시글", "내용", 3L);
        postRepository.save(post);
        Long postId = post.getId();

        commentRepository.save(Comment.create(postId, null, myId, "내 댓글"));
        commentRepository.save(Comment.create(postId, null, otherId, "다른 사람 댓글"));

        MyCommentsResponse response = useCase.execute(myId, 0, 20);

        assertThat(response.comments()).hasSize(1);
        assertThat(response.totalCount()).isEqualTo(1);
        assertThat(response.comments().getFirst().content()).isEqualTo("내 댓글");
    }

    @Test
    void 빈_결과_빈_리스트_반환() {
        Long memberId = 1L;

        MyCommentsResponse response = useCase.execute(memberId, 0, 20);

        assertThat(response.comments()).isEmpty();
        assertThat(response.totalCount()).isZero();
    }

    @Test
    void 페이징_동작_확인() {
        Long memberId = 1L;
        Post post = Post.create(1L, "테스트 게시글", "내용", 2L);
        postRepository.save(post);
        Long postId = post.getId();

        for (int i = 1; i <= 5; i++) {
            commentRepository.save(Comment.create(postId, null, memberId, "댓글 " + i));
        }

        MyCommentsResponse response = useCase.execute(memberId, 0, 2);

        assertThat(response.comments()).hasSize(2);
        assertThat(response.totalCount()).isEqualTo(5);
    }

    @Test
    void postTitle_포함_확인() {
        Long memberId = 1L;
        Post post = Post.create(1L, "원본 게시글 제목", "내용", 2L);
        postRepository.save(post);
        Long postId = post.getId();

        commentRepository.save(Comment.create(postId, null, memberId, "댓글입니다"));

        MyCommentsResponse response = useCase.execute(memberId, 0, 20);

        assertThat(response.comments().getFirst().postTitle()).isEqualTo("원본 게시글 제목");
        assertThat(response.comments().getFirst().postId()).isEqualTo(postId);
    }

    @Test
    void 삭제된_게시글의_댓글은_postTitle이_삭제된_게시글() {
        Long memberId = 1L;
        Long deletedPostId = 999L;

        commentRepository.save(Comment.create(deletedPostId, null, memberId, "고아 댓글"));

        MyCommentsResponse response = useCase.execute(memberId, 0, 20);

        assertThat(response.comments().getFirst().postTitle()).isEqualTo("삭제된 게시글");
    }

    @Test
    void mentions_포함_확인() {
        Long memberId = 1L;
        Post post = Post.create(1L, "테스트 게시글", "내용", 2L);
        postRepository.save(post);
        Long postId = post.getId();

        commentRepository.save(Comment.create(postId, null, memberId, "@홍길동 @김철수 안녕하세요"));

        MyCommentsResponse response = useCase.execute(memberId, 0, 20);

        assertThat(response.comments().getFirst().mentions()).containsExactly("홍길동", "김철수");
    }
}
