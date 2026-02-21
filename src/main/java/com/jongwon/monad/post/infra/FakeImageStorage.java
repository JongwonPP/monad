package com.jongwon.monad.post.infra;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.post.domain.ImageStorage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("local")
public class FakeImageStorage implements ImageStorage {

    private final Map<String, byte[]> store = new ConcurrentHashMap<>();

    @Override
    public void store(String storedFilename, byte[] data) {
        store.put(storedFilename, data);
    }

    @Override
    public byte[] load(String storedFilename) {
        byte[] data = store.get(storedFilename);
        if (data == null) {
            throw new EntityNotFoundException("이미지 파일을 찾을 수 없습니다.");
        }
        return data;
    }

    @Override
    public void delete(String storedFilename) {
        store.remove(storedFilename);
    }
}
