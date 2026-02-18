package com.jongwon.monad.comment.mycomments;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyCommentsUseCase {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public MyCommentsUseCase(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public MyCommentsResponse execute(Long memberId, int page, int size) {
        List<Comment> comments = commentRepository.findAllByMemberId(memberId, page, size);
        long totalCount = commentRepository.countByMemberId(memberId);

        List<MyCommentsResponse.CommentItem> items = comments.stream()
                .map(comment -> {
                    String postTitle = postRepository.findById(comment.getPostId())
                            .map(post -> post.getTitle())
                            .orElse("삭제된 게시글");
                    return new MyCommentsResponse.CommentItem(
                            comment.getId(),
                            comment.getPostId(),
                            postTitle,
                            comment.getContent(),
                            comment.getMentions(),
                            comment.getCreatedAt()
                    );
                })
                .toList();

        return new MyCommentsResponse(items, totalCount, page, size);
    }
}
