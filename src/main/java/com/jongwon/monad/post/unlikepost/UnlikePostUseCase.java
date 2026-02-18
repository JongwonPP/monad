package com.jongwon.monad.post.unlikepost;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.PostLikeRepository;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class UnlikePostUseCase {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public UnlikePostUseCase(PostRepository postRepository, PostLikeRepository postLikeRepository) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
    }

    public UnlikePostResponse execute(Long postId, Long memberId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        postLikeRepository.findByPostIdAndMemberId(postId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요하지 않은 게시글입니다."));

        postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);

        long likeCount = postLikeRepository.countByPostId(postId);

        return new UnlikePostResponse(postId, false, likeCount);
    }
}
