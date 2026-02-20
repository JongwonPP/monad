CREATE TABLE post_image (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id           BIGINT       NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename   VARCHAR(255) NOT NULL,
    content_type      VARCHAR(50)  NOT NULL,
    file_size         BIGINT       NOT NULL,
    created_at        TIMESTAMP    NOT NULL
);

CREATE INDEX idx_post_image_post_id ON post_image(post_id);
CREATE UNIQUE INDEX idx_post_image_stored_filename ON post_image(stored_filename);
