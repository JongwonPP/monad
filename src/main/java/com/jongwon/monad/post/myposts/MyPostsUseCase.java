package com.jongwon.monad.post.myposts;

import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyPostsUseCase {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    public MyPostsUseCase(PostRepository postRepository, BoardRepository boardRepository) {
        this.postRepository = postRepository;
        this.boardRepository = boardRepository;
    }

    public MyPostsResponse execute(Long memberId, int page, int size) {
        List<Post> posts = postRepository.findAllByMemberId(memberId, page, size);
        long totalCount = postRepository.countByMemberId(memberId);

        List<MyPostsResponse.PostItem> items = posts.stream()
                .map(post -> {
                    String boardName = boardRepository.findById(post.getBoardId())
                            .map(board -> board.getName())
                            .orElse("삭제된 게시판");
                    return new MyPostsResponse.PostItem(
                            post.getId(),
                            post.getBoardId(),
                            boardName,
                            post.getTitle(),
                            post.getViewCount(),
                            post.getCreatedAt()
                    );
                })
                .toList();

        return new MyPostsResponse(items, totalCount, page, size);
    }
}
