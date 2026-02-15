package com.jongwon.monad.comment.createcomment;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateCommentUseCase {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public CreateCommentUseCase(CommentRepository commentRepository,
                                PostRepository postRepository,
                                MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    public CreateCommentResponse execute(Long postId, Long memberId, CreateCommentRequest request) {
        postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        Comment comment = Comment.create(postId, null, memberId, request.content());

        List<String> validMentions = comment.getMentions().stream()
                .filter(memberRepository::existsByNickname)
                .toList();
        comment.filterMentions(validMentions);

        Comment saved = commentRepository.save(comment);

        return new CreateCommentResponse(
                saved.getId(),
                saved.getPostId(),
                saved.getMemberId(),
                saved.getContent(),
                saved.getMentions(),
                saved.getCreatedAt()
        );
    }
}
