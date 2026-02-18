package com.jongwon.monad.post.domain;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post save(Post post);

    Optional<Post> findById(Long id);

    List<Post> findAllByBoardId(Long boardId, int page, int size, PostSortType sortType);

    long countByBoardId(Long boardId);

    void deleteById(Long id);

    // 검색
    List<Post> searchByKeyword(String keyword, int page, int size);

    long countByKeyword(String keyword);

    List<Post> searchByBoardIdAndKeyword(Long boardId, String keyword, int page, int size);

    long countByBoardIdAndKeyword(Long boardId, String keyword);

    // 마이페이지
    List<Post> findAllByMemberId(Long memberId, int page, int size);

    long countByMemberId(Long memberId);
}
