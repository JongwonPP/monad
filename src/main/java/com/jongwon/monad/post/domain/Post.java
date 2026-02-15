package com.jongwon.monad.post.domain;

import java.time.LocalDateTime;

public class Post {

    private Long id;
    private Long boardId;
    private String title;
    private String content;
    private String author;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Post(Long boardId, String title, String content, String author,
                 int viewCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Post create(Long boardId, String title, String content, String author) {
        if (boardId == null) {
            throw new IllegalArgumentException("게시판 ID는 필수입니다");
        }
        validateTitle(title);
        validateContent(content);
        validateAuthor(author);
        LocalDateTime now = LocalDateTime.now();
        return new Post(boardId, title, content, author, 0, now, now);
    }

    public void update(String title, String content) {
        validateTitle(title);
        validateContent(content);
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void assignId(Long id) {
        this.id = id;
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("게시글 제목은 빈 값일 수 없습니다");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("게시글 제목은 200자를 초과할 수 없습니다");
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("게시글 본문은 빈 값일 수 없습니다");
        }
    }

    private static void validateAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("작성자는 빈 값일 수 없습니다");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getBoardId() {
        return boardId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public int getViewCount() {
        return viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
