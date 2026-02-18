package com.jongwon.monad.post.likepost;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.PostLike;
import com.jongwon.monad.post.domain.PostLikeRepository;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class LikePostUseCase {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public LikePostUseCase(PostRepository postRepository, PostLikeRepository postLikeRepository) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
    }

    public LikePostResponse execute(Long postId, Long memberId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        postLikeRepository.findByPostIdAndMemberId(postId, memberId)
                .ifPresent(like -> {
                    throw new IllegalArgumentException("이미 좋아요한 게시글입니다.");
                });

        PostLike postLike = PostLike.create(postId, memberId);
        postLikeRepository.save(postLike);

        long likeCount = postLikeRepository.countByPostId(postId);

        return new LikePostResponse(postId, true, likeCount);
    }
}
