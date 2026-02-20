package com.jongwon.monad.post.uploadimage;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.ImageStorage;
import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostImage;
import com.jongwon.monad.post.domain.PostImageRepository;
import com.jongwon.monad.post.domain.PostRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UploadImageUseCase {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final ImageStorage imageStorage;

    public UploadImageUseCase(PostRepository postRepository,
                              PostImageRepository postImageRepository,
                              ImageStorage imageStorage) {
        this.postRepository = postRepository;
        this.postImageRepository = postImageRepository;
        this.imageStorage = imageStorage;
    }

    public UploadImageResponse execute(Long postId, Long memberId,
                                       String originalFilename, String contentType,
                                       long fileSize, byte[] data) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 게시글만 이미지를 첨부할 수 있습니다");
        }

        if (postImageRepository.countByPostId(postId) >= 5) {
            throw new IllegalArgumentException("게시글 당 이미지는 최대 5장까지 첨부할 수 있습니다");
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String storedFilename = UUID.randomUUID() + extension;

        PostImage postImage = PostImage.create(postId, originalFilename, storedFilename, contentType, fileSize);
        imageStorage.store(storedFilename, data);
        PostImage saved = postImageRepository.save(postImage);

        return new UploadImageResponse(
                saved.getId(),
                saved.getPostId(),
                saved.getOriginalFilename(),
                saved.getStoredFilename(),
                "/api/v1/images/" + saved.getStoredFilename(),
                saved.getFileSize(),
                saved.getContentType(),
                saved.getCreatedAt()
        );
    }
}
