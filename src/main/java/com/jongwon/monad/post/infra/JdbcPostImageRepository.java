package com.jongwon.monad.post.infra;

import com.jongwon.monad.post.domain.PostImage;
import com.jongwon.monad.post.domain.PostImageRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("prod")
public class JdbcPostImageRepository implements PostImageRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<PostImage> rowMapper = (rs, rowNum) -> PostImage.reconstruct(
            rs.getLong("id"),
            rs.getLong("post_id"),
            rs.getString("original_filename"),
            rs.getString("stored_filename"),
            rs.getString("content_type"),
            rs.getLong("file_size"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public JdbcPostImageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PostImage save(PostImage postImage) {
        if (postImage.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO post_image (post_id, original_filename, stored_filename, content_type, file_size, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setLong(1, postImage.getPostId());
                ps.setString(2, postImage.getOriginalFilename());
                ps.setString(3, postImage.getStoredFilename());
                ps.setString(4, postImage.getContentType());
                ps.setLong(5, postImage.getFileSize());
                ps.setTimestamp(6, Timestamp.valueOf(postImage.getCreatedAt()));
                return ps;
            }, keyHolder);
            postImage.assignId(keyHolder.getKey().longValue());
        }
        return postImage;
    }

    @Override
    public Optional<PostImage> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM post_image WHERE id = ?", rowMapper, id)
                .stream().findFirst();
    }

    @Override
    public Optional<PostImage> findByStoredFilename(String storedFilename) {
        return jdbcTemplate.query("SELECT * FROM post_image WHERE stored_filename = ?", rowMapper, storedFilename)
                .stream().findFirst();
    }

    @Override
    public List<PostImage> findAllByPostId(Long postId) {
        return jdbcTemplate.query("SELECT * FROM post_image WHERE post_id = ? ORDER BY created_at ASC", rowMapper, postId);
    }

    @Override
    public int countByPostId(Long postId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post_image WHERE post_id = ?", Integer.class, postId);
        return count != null ? count : 0;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM post_image WHERE id = ?", id);
    }

    @Override
    public void deleteAllByPostId(Long postId) {
        jdbcTemplate.update("DELETE FROM post_image WHERE post_id = ?", postId);
    }
}
