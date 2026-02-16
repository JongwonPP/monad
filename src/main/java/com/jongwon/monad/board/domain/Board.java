package com.jongwon.monad.board.domain;

import java.time.LocalDateTime;

public class Board {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Board(String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Board create(String name, String description) {
        validateName(name);
        LocalDateTime now = LocalDateTime.now();
        return new Board(name, description, now, now);
    }

    public static Board reconstruct(Long id, String name, String description,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        Board board = new Board(name, description, createdAt, updatedAt);
        board.id = id;
        return board;
    }

    public void update(String name, String description) {
        validateName(name);
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void assignId(Long id) {
        this.id = id;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("게시판 이름은 빈 값일 수 없습니다");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("게시판 이름은 50자를 초과할 수 없습니다");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
