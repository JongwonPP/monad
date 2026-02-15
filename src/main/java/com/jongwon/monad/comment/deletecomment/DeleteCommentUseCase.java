package com.jongwon.monad.comment.deletecomment;

import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DeleteCommentUseCase {

    private final CommentRepository commentRepository;

    public DeleteCommentUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void execute(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다: " + commentId));

        commentRepository.deleteAllByParentId(commentId);
        commentRepository.deleteById(commentId);
    }
}
