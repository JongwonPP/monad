package com.jongwon.monad.comment.listcomments;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ListCommentsUseCase {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    public ListCommentsUseCase(CommentRepository commentRepository,
                               MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
    }

    public ListCommentsResponse execute(Long postId) {
        List<Comment> all = commentRepository.findAllByPostId(postId);
        long totalCount = all.size();

        List<Comment> parents = all.stream()
                .filter(c -> c.getParentId() == null)
                .toList();

        Map<Long, List<Comment>> repliesByParentId = all.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentId));

        List<ListCommentsResponse.CommentItem> commentItems = parents.stream()
                .map(parent -> {
                    String parentNickname = lookupNickname(parent.getMemberId());

                    List<ListCommentsResponse.ReplyItem> replies = repliesByParentId
                            .getOrDefault(parent.getId(), List.of())
                            .stream()
                            .map(reply -> new ListCommentsResponse.ReplyItem(
                                    reply.getId(),
                                    reply.getMemberId(),
                                    lookupNickname(reply.getMemberId()),
                                    reply.getContent(),
                                    reply.getMentions(),
                                    reply.getCreatedAt()
                            ))
                            .toList();

                    return new ListCommentsResponse.CommentItem(
                            parent.getId(),
                            parent.getMemberId(),
                            parentNickname,
                            parent.getContent(),
                            parent.getMentions(),
                            parent.getCreatedAt(),
                            replies
                    );
                })
                .toList();

        return new ListCommentsResponse(commentItems, totalCount);
    }

    private String lookupNickname(Long memberId) {
        return memberRepository.findById(memberId)
                .map(member -> member.getNickname())
                .orElse("알 수 없음");
    }
}
