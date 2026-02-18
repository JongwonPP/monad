package com.jongwon.monad.post.listposts;

import com.jongwon.monad.post.domain.PostSortType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListPostsController {

    private final ListPostsUseCase listPostsUseCase;

    public ListPostsController(ListPostsUseCase listPostsUseCase) {
        this.listPostsUseCase = listPostsUseCase;
    }

    @GetMapping("/api/v1/boards/{boardId}/posts")
    public ResponseEntity<ListPostsResponse> listPosts(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "latest") String sort) {
        PostSortType sortType = PostSortType.from(sort);
        ListPostsResponse response = listPostsUseCase.execute(boardId, page, size, sortType);
        return ResponseEntity.ok(response);
    }
}
