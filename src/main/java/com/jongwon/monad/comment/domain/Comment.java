package com.jongwon.monad.comment.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Comment {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([가-힣a-zA-Z0-9_]{2,20})");

    private Long id;
    private Long postId;
    private Long parentId;
    private Long memberId;
    private String content;
    private List<String> mentions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Comment(Long postId, Long parentId, Long memberId, String content,
                    List<String> mentions, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.parentId = parentId;
        this.memberId = memberId;
        this.content = content;
        this.mentions = mentions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Comment create(Long postId, Long parentId, Long memberId, String content) {
        if (postId == null) {
            throw new IllegalArgumentException("게시글 ID는 필수입니다");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다");
        }
        validateContent(content);
        LocalDateTime now = LocalDateTime.now();
        List<String> mentions = parseMentions(content);
        return new Comment(postId, parentId, memberId, content, mentions, now, now);
    }

    public void update(String content) {
        validateContent(content);
        this.content = content;
        this.mentions = parseMentions(content);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isReply() {
        return parentId != null;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void filterMentions(List<String> validNicknames) {
        this.mentions = this.mentions.stream()
                .filter(validNicknames::contains)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> getMentions() {
        return Collections.unmodifiableList(mentions);
    }

    public static List<String> parseMentions(String content) {
        if (content == null) {
            return new ArrayList<>();
        }
        Matcher matcher = MENTION_PATTERN.matcher(content);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            String nickname = matcher.group(1);
            if (!result.contains(nickname)) {
                result.add(nickname);
            }
        }
        return result;
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("댓글 내용은 빈 값일 수 없습니다");
        }
        if (content.length() > 500) {
            throw new IllegalArgumentException("댓글 내용은 500자를 초과할 수 없습니다");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getParentId() {
        return parentId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
