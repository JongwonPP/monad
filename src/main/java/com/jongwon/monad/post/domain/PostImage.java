package com.jongwon.monad.post.domain;

import java.time.LocalDateTime;
import java.util.Set;

public class PostImage {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private Long id;
    private Long postId;
    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private long fileSize;
    private LocalDateTime createdAt;

    private PostImage(Long postId, String originalFilename, String storedFilename,
                      String contentType, long fileSize, LocalDateTime createdAt) {
        this.postId = postId;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
    }

    public static PostImage create(Long postId, String originalFilename, String storedFilename,
                                   String contentType, long fileSize) {
        if (postId == null) {
            throw new IllegalArgumentException("게시글 ID는 필수입니다");
        }
        validateOriginalFilename(originalFilename);
        validateStoredFilename(storedFilename);
        validateContentType(contentType);
        validateFileSize(fileSize);
        return new PostImage(postId, originalFilename, storedFilename, contentType, fileSize, LocalDateTime.now());
    }

    public static PostImage reconstruct(Long id, Long postId, String originalFilename, String storedFilename,
                                        String contentType, long fileSize, LocalDateTime createdAt) {
        PostImage postImage = new PostImage(postId, originalFilename, storedFilename, contentType, fileSize, createdAt);
        postImage.id = id;
        return postImage;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    private static void validateOriginalFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("원본 파일명은 빈 값일 수 없습니다");
        }
    }

    private static void validateStoredFilename(String storedFilename) {
        if (storedFilename == null || storedFilename.isBlank()) {
            throw new IllegalArgumentException("저장 파일명은 빈 값일 수 없습니다");
        }
    }

    private static void validateContentType(String contentType) {
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("허용되지 않는 이미지 형식입니다. 허용: JPEG, PNG, GIF, WEBP");
        }
    }

    private static void validateFileSize(long fileSize) {
        if (fileSize <= 0) {
            throw new IllegalArgumentException("파일 크기는 0보다 커야 합니다");
        }
        if (fileSize > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
