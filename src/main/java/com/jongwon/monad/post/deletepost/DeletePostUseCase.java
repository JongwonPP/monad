package com.jongwon.monad.post.deletepost;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.ImageStorage;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostImage;
import com.jongwon.monad.post.domain.PostImageRepository;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeletePostUseCase {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final ImageStorage imageStorage;

    public DeletePostUseCase(PostRepository postRepository, PostImageRepository postImageRepository,
                             ImageStorage imageStorage) {
        this.postRepository = postRepository;
        this.postImageRepository = postImageRepository;
        this.imageStorage = imageStorage;
    }

    public void execute(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 글만 삭제할 수 있습니다");
        }

        List<PostImage> images = postImageRepository.findAllByPostId(postId);
        for (PostImage image : images) {
            imageStorage.delete(image.getStoredFilename());
        }
        postImageRepository.deleteAllByPostId(postId);

        postRepository.deleteById(postId);
    }
}
