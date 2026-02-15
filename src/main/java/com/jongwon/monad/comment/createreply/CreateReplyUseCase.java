package com.jongwon.monad.comment.createreply;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateReplyUseCase {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public CreateReplyUseCase(CommentRepository commentRepository,
                              PostRepository postRepository,
                              MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    public CreateReplyResponse execute(Long postId, Long commentId, Long memberId, CreateReplyRequest request) {
        postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다: " + commentId));

        if (parent.isReply()) {
            throw new IllegalArgumentException("대댓글에는 답글을 달 수 없습니다");
        }

        Comment reply = Comment.create(postId, commentId, memberId, request.content());

        List<String> validMentions = reply.getMentions().stream()
                .filter(memberRepository::existsByNickname)
                .toList();
        reply.filterMentions(validMentions);

        Comment saved = commentRepository.save(reply);

        return new CreateReplyResponse(
                saved.getId(),
                saved.getPostId(),
                saved.getParentId(),
                saved.getMemberId(),
                saved.getContent(),
                saved.getMentions(),
                saved.getCreatedAt()
        );
    }
}
