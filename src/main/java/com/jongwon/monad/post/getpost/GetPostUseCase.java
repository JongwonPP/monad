package com.jongwon.monad.post.getpost;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class GetPostUseCase {

    private final PostRepository postRepository;

    public GetPostUseCase(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public GetPostResponse execute(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        post.increaseViewCount();
        postRepository.save(post);

        return new GetPostResponse(
                post.getId(),
                post.getBoardId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}
