package com.jongwon.monad.post.updatepost;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdatePostUseCase {

    private final PostRepository postRepository;

    public UpdatePostUseCase(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public UpdatePostResponse execute(Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        post.update(request.title(), request.content());
        Post saved = postRepository.save(post);

        return new UpdatePostResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getUpdatedAt()
        );
    }
}
