package com.jongwon.monad.post.getimage;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.ImageStorage;
import com.jongwon.monad.post.domain.PostImage;
import com.jongwon.monad.post.domain.PostImageRepository;
import org.springframework.stereotype.Service;

@Service
public class GetImageUseCase {

    private final PostImageRepository postImageRepository;
    private final ImageStorage imageStorage;

    public GetImageUseCase(PostImageRepository postImageRepository, ImageStorage imageStorage) {
        this.postImageRepository = postImageRepository;
        this.imageStorage = imageStorage;
    }

    public record ImageData(byte[] data, String contentType) {}

    public ImageData execute(String storedFilename) {
        PostImage postImage = postImageRepository.findByStoredFilename(storedFilename)
                .orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다."));

        byte[] data = imageStorage.load(storedFilename);

        return new ImageData(data, postImage.getContentType());
    }
}
