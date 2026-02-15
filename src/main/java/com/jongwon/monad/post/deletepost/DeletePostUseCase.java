package com.jongwon.monad.post.deletepost;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class DeletePostUseCase {

    private final PostRepository postRepository;

    public DeletePostUseCase(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void execute(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 글만 삭제할 수 있습니다");
        }

        postRepository.deleteById(postId);
    }
}
