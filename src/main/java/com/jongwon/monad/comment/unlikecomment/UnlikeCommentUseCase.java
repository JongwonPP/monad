package com.jongwon.monad.comment.unlikecomment;

import com.jongwon.monad.comment.domain.CommentLikeRepository;
import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UnlikeCommentUseCase {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public UnlikeCommentUseCase(CommentRepository commentRepository, CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    public UnlikeCommentResponse execute(Long commentId, Long memberId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        commentLikeRepository.findByCommentIdAndMemberId(commentId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요하지 않은 댓글입니다."));

        commentLikeRepository.deleteByCommentIdAndMemberId(commentId, memberId);

        long likeCount = commentLikeRepository.countByCommentId(commentId);

        return new UnlikeCommentResponse(commentId, false, likeCount);
    }
}
