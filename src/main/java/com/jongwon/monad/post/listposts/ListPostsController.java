package com.jongwon.monad.post.listposts;

import com.jongwon.monad.post.domain.PostSortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post", description = "게시글 API")
@RestController
public class ListPostsController {

    private final ListPostsUseCase listPostsUseCase;

    public ListPostsController(ListPostsUseCase listPostsUseCase) {
        this.listPostsUseCase = listPostsUseCase;
    }

    @Operation(summary = "게시글 목록")
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
