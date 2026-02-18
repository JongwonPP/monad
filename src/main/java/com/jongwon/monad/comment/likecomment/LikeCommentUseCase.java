package com.jongwon.monad.comment.likecomment;

import com.jongwon.monad.comment.domain.CommentLike;
import com.jongwon.monad.comment.domain.CommentLikeRepository;
import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LikeCommentUseCase {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public LikeCommentUseCase(CommentRepository commentRepository, CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    public LikeCommentResponse execute(Long commentId, Long memberId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        commentLikeRepository.findByCommentIdAndMemberId(commentId, memberId)
                .ifPresent(like -> {
                    throw new IllegalArgumentException("이미 좋아요한 댓글입니다.");
                });

        CommentLike commentLike = CommentLike.create(commentId, memberId);
        commentLikeRepository.save(commentLike);

        long likeCount = commentLikeRepository.countByCommentId(commentId);

        return new LikeCommentResponse(commentId, true, likeCount);
    }
}
