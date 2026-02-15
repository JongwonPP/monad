package com.jongwon.monad.post.createpost;

import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class CreatePostUseCase {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    public CreatePostUseCase(PostRepository postRepository, BoardRepository boardRepository) {
        this.postRepository = postRepository;
        this.boardRepository = boardRepository;
    }

    public CreatePostResponse execute(Long boardId, Long memberId, CreatePostRequest request) {
        boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다."));

        Post post = Post.create(boardId, request.title(), request.content(), memberId);
        Post saved = postRepository.save(post);

        return new CreatePostResponse(
                saved.getId(),
                saved.getBoardId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getMemberId(),
                saved.getCreatedAt()
        );
    }
}
