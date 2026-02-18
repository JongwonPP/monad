package com.jongwon.monad.post.searchposts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchPostsController {

    private final SearchPostsUseCase searchPostsUseCase;

    public SearchPostsController(SearchPostsUseCase searchPostsUseCase) {
        this.searchPostsUseCase = searchPostsUseCase;
    }

    @GetMapping("/api/v1/posts/search")
    public ResponseEntity<SearchPostsResponse> searchPosts(
            @RequestParam String keyword,
            @RequestParam(required = false) Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchPostsResponse response = searchPostsUseCase.execute(keyword, boardId, page, size);
        return ResponseEntity.ok(response);
    }
}
