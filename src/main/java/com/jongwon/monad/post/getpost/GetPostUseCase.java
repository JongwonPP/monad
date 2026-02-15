package com.jongwon.monad.post.getpost;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class GetPostUseCase {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public GetPostUseCase(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    public GetPostResponse execute(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        post.increaseViewCount();
        postRepository.save(post);

        String nickname = memberRepository.findById(post.getMemberId())
                .map(member -> member.getNickname())
                .orElse("알 수 없음");

        return new GetPostResponse(
                post.getId(),
                post.getBoardId(),
                post.getTitle(),
                post.getContent(),
                post.getMemberId(),
                nickname,
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}
