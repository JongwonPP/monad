package com.jongwon.monad.post.listposts;

import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListPostsUseCase {

    private final PostRepository postRepository;

    public ListPostsUseCase(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ListPostsResponse execute(Long boardId, int page, int size) {
        List<Post> posts = postRepository.findAllByBoardId(boardId, page, size);
        long totalCount = postRepository.countByBoardId(boardId);

        List<ListPostsResponse.PostItem> items = posts.stream()
                .map(post -> new ListPostsResponse.PostItem(
                        post.getId(),
                        post.getTitle(),
                        post.getAuthor(),
                        post.getViewCount(),
                        post.getCreatedAt()
                ))
                .toList();

        return new ListPostsResponse(items, totalCount, page, size);
    }
}
