package com.jongwon.monad.member.domain;

import java.time.LocalDateTime;

public class Member {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Member(String email, String password, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Member create(String email, String password, String nickname) {
        validateEmail(email);
        validateNickname(nickname);
        LocalDateTime now = LocalDateTime.now();
        return new Member(email, password, nickname, now, now);
    }

    public static Member reconstruct(Long id, String email, String password,
                                      String nickname, LocalDateTime createdAt,
                                      LocalDateTime updatedAt) {
        Member member = new Member(email, password, nickname, createdAt, updatedAt);
        member.id = id;
        return member;
    }

    public void updateProfile(String nickname) {
        validateNickname(nickname);
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void assignId(Long id) {
        this.id = id;
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 빈 값일 수 없습니다");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("이메일은 100자를 초과할 수 없습니다");
        }
    }

    public static void validateRawPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 빈 값일 수 없습니다");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다");
        }
        if (password.length() > 100) {
            throw new IllegalArgumentException("비밀번호는 100자를 초과할 수 없습니다");
        }
    }

    private static void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 빈 값일 수 없습니다");
        }
        if (nickname.length() < 2) {
            throw new IllegalArgumentException("닉네임은 2자 이상이어야 합니다");
        }
        if (nickname.length() > 20) {
            throw new IllegalArgumentException("닉네임은 20자를 초과할 수 없습니다");
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
