package com.jongwon.monad.comment.listcomments;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ListCommentsUseCase {

    private final CommentRepository commentRepository;

    public ListCommentsUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
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
                    List<ListCommentsResponse.ReplyItem> replies = repliesByParentId
                            .getOrDefault(parent.getId(), List.of())
                            .stream()
                            .map(reply -> new ListCommentsResponse.ReplyItem(
                                    reply.getId(),
                                    reply.getAuthor(),
                                    reply.getContent(),
                                    reply.getMentions(),
                                    reply.getCreatedAt()
                            ))
                            .toList();

                    return new ListCommentsResponse.CommentItem(
                            parent.getId(),
                            parent.getAuthor(),
                            parent.getContent(),
                            parent.getMentions(),
                            parent.getCreatedAt(),
                            replies
                    );
                })
                .toList();

        return new ListCommentsResponse(commentItems, totalCount);
    }
}
