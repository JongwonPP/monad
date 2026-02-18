package com.jongwon.monad.post.domain;

public enum PostSortType {
    LATEST,
    OLDEST,
    VIEWS,
    LIKES;

    public static PostSortType from(String value) {
        if (value == null || value.isBlank()) {
            return LATEST;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 정렬 기준입니다: " + value);
        }
    }
}
