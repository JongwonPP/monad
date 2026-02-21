package com.jongwon.monad.comment.updatecomment;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.global.exception.AuthorizationException;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateCommentUseCase {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    public UpdateCommentUseCase(CommentRepository commentRepository,
                                MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
    }

    public UpdateCommentResponse execute(Long commentId, Long memberId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다: " + commentId));

        if (!comment.getMemberId().equals(memberId)) {
            throw new AuthorizationException("본인의 댓글만 수정할 수 있습니다");
        }

        comment.update(request.content());

        List<String> validMentions = comment.getMentions().stream()
                .filter(memberRepository::existsByNickname)
                .toList();
        comment.filterMentions(validMentions);

        Comment saved = commentRepository.save(comment);

        return new UpdateCommentResponse(
                saved.getId(),
                saved.getContent(),
                saved.getMentions(),
                saved.getUpdatedAt()
        );
    }
}
