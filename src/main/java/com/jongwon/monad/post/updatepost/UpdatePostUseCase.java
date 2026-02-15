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

    public UpdatePostResponse execute(Long postId, Long memberId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 글만 수정할 수 있습니다");
        }

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
