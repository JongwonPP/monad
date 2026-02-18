CREATE TABLE post_like (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT    NOT NULL,
    member_id  BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,
    UNIQUE (post_id, member_id)
);

CREATE TABLE comment_like (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT    NOT NULL,
    member_id  BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,
    UNIQUE (comment_id, member_id)
);

CREATE INDEX idx_post_like_post_id ON post_like(post_id);
CREATE INDEX idx_post_like_member_id ON post_like(member_id);
CREATE INDEX idx_comment_like_comment_id ON comment_like(comment_id);
CREATE INDEX idx_comment_like_member_id ON comment_like(member_id);

CREATE INDEX idx_post_member_id ON post(member_id);
CREATE INDEX idx_comment_member_id ON comment(member_id);
