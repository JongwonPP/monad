package com.jongwon.monad.post.deletepost;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class DeletePostUseCase {

    private final PostRepository postRepository;

    public DeletePostUseCase(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void execute(Long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        postRepository.deleteById(postId);
    }
}
