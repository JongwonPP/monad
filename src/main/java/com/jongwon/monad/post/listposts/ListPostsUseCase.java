package com.jongwon.monad.post.listposts;

import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostLikeRepository;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.domain.PostSortType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListPostsUseCase {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;

    public ListPostsUseCase(PostRepository postRepository, MemberRepository memberRepository,
                            PostLikeRepository postLikeRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.postLikeRepository = postLikeRepository;
    }

    public ListPostsResponse execute(Long boardId, int page, int size, PostSortType sortType) {
        List<Post> posts = postRepository.findAllByBoardId(boardId, page, size, sortType);
        long totalCount = postRepository.countByBoardId(boardId);

        List<ListPostsResponse.PostItem> items = posts.stream()
                .map(post -> {
                    String nickname = memberRepository.findById(post.getMemberId())
                            .map(member -> member.getNickname())
                            .orElse("알 수 없음");
                    long likeCount = postLikeRepository.countByPostId(post.getId());
                    return new ListPostsResponse.PostItem(
                            post.getId(),
                            post.getTitle(),
                            post.getMemberId(),
                            nickname,
                            post.getViewCount(),
                            likeCount,
                            post.getCreatedAt()
                    );
                })
                .toList();

        return new ListPostsResponse(items, totalCount, page, size);
    }
}
