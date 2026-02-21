package com.jongwon.monad.post.deleteimage;

import com.jongwon.monad.global.exception.AuthorizationException;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.ImageStorage;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostImage;
import com.jongwon.monad.post.domain.PostImageRepository;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteImageUseCase {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final ImageStorage imageStorage;

    public DeleteImageUseCase(PostRepository postRepository, PostImageRepository postImageRepository,
                              ImageStorage imageStorage) {
        this.postRepository = postRepository;
        this.postImageRepository = postImageRepository;
        this.imageStorage = imageStorage;
    }

    public void execute(Long postId, Long imageId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getMemberId().equals(memberId)) {
            throw new AuthorizationException("본인의 게시글 이미지만 삭제할 수 있습니다");
        }

        PostImage postImage = postImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다."));

        if (!postImage.getPostId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글의 이미지가 아닙니다");
        }

        imageStorage.delete(postImage.getStoredFilename());
        postImageRepository.deleteById(imageId);
    }
}
