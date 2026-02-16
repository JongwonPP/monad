CREATE TABLE member (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    nickname   VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE TABLE board (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);

CREATE TABLE post (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id   BIGINT       NOT NULL,
    title      VARCHAR(200) NOT NULL,
    content    CLOB         NOT NULL,
    member_id  BIGINT       NOT NULL,
    view_count INT          NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE TABLE comment (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT       NOT NULL,
    parent_id  BIGINT,
    member_id  BIGINT       NOT NULL,
    content    VARCHAR(500) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE INDEX idx_post_board_id ON post(board_id);
CREATE INDEX idx_post_created_at ON post(created_at);
CREATE INDEX idx_comment_post_id ON comment(post_id);
CREATE INDEX idx_comment_parent_id ON comment(parent_id);
