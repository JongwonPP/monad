package com.jongwon.monad.post.infra;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.ImageStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Profile("prod")
public class LocalImageStorage implements ImageStorage {

    private final Path storagePath;

    public LocalImageStorage(@Value("${image.storage.path}") String storagePath) {
        this.storagePath = Path.of(storagePath);
    }

    @PostConstruct
    void init() throws IOException {
        Files.createDirectories(storagePath);
    }

    @Override
    public void store(String storedFilename, byte[] data) {
        try {
            Files.write(storagePath.resolve(storedFilename), data);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패했습니다", e);
        }
    }

    @Override
    public byte[] load(String storedFilename) {
        try {
            return Files.readAllBytes(storagePath.resolve(storedFilename));
        } catch (IOException e) {
            throw new EntityNotFoundException("이미지를 찾을 수 없습니다: " + storedFilename);
        }
    }

    @Override
    public void delete(String storedFilename) {
        try {
            Files.deleteIfExists(storagePath.resolve(storedFilename));
        } catch (IOException e) {
            throw new RuntimeException("이미지 삭제에 실패했습니다", e);
        }
    }
}
