package com.jongwon.monad.post.searchposts;

import com.jongwon.monad.board.domain.BoardRepository;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchPostsUseCase {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    public SearchPostsUseCase(PostRepository postRepository,
                              MemberRepository memberRepository,
                              BoardRepository boardRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
    }

    public SearchPostsResponse execute(String keyword, Long boardId, int page, int size) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색 키워드는 필수입니다");
        }

        List<Post> posts;
        long totalCount;

        if (boardId == null) {
            posts = postRepository.searchByKeyword(keyword, page, size);
            totalCount = postRepository.countByKeyword(keyword);
        } else {
            posts = postRepository.searchByBoardIdAndKeyword(boardId, keyword, page, size);
            totalCount = postRepository.countByBoardIdAndKeyword(boardId, keyword);
        }

        List<SearchPostsResponse.PostItem> items = posts.stream()
                .map(post -> {
                    String nickname = memberRepository.findById(post.getMemberId())
                            .map(member -> member.getNickname())
                            .orElse("알 수 없음");
                    String boardName = boardRepository.findById(post.getBoardId())
                            .map(board -> board.getName())
                            .orElse("알 수 없음");
                    return new SearchPostsResponse.PostItem(
                            post.getId(),
                            post.getBoardId(),
                            boardName,
                            post.getTitle(),
                            post.getMemberId(),
                            nickname,
                            post.getViewCount(),
                            post.getCreatedAt()
                    );
                })
                .toList();

        return new SearchPostsResponse(items, totalCount, page, size);
    }
}
