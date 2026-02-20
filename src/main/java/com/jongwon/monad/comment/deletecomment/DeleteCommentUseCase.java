package com.jongwon.monad.comment.deletecomment;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.global.exception.AuthorizationException;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DeleteCommentUseCase {

    private final CommentRepository commentRepository;

    public DeleteCommentUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void execute(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다: " + commentId));

        if (!comment.getMemberId().equals(memberId)) {
            throw new AuthorizationException("본인의 댓글만 삭제할 수 있습니다");
        }

        commentRepository.deleteAllByParentId(commentId);
        commentRepository.deleteById(commentId);
    }
}
