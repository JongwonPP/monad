package com.jongwon.monad.post.domain;

public interface ImageStorage {

    void store(String storedFilename, byte[] data);

    byte[] load(String storedFilename);

    void delete(String storedFilename);
}
