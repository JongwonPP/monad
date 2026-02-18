package com.jongwon.monad.comment.infra;

import com.jongwon.monad.comment.domain.Comment;
import com.jongwon.monad.comment.domain.CommentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("prod")
public class JdbcCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Comment> rowMapper = (rs, rowNum) -> {
        long parentIdValue = rs.getLong("parent_id");
        Long parentId = rs.wasNull() ? null : parentIdValue;
        return Comment.reconstruct(
                rs.getLong("id"),
                rs.getLong("post_id"),
                parentId,
                rs.getLong("member_id"),
                rs.getString("content"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    };

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO comment (post_id, parent_id, member_id, content, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setLong(1, comment.getPostId());
                if (comment.getParentId() != null) {
                    ps.setLong(2, comment.getParentId());
                } else {
                    ps.setNull(2, Types.BIGINT);
                }
                ps.setLong(3, comment.getMemberId());
                ps.setString(4, comment.getContent());
                ps.setTimestamp(5, Timestamp.valueOf(comment.getCreatedAt()));
                ps.setTimestamp(6, Timestamp.valueOf(comment.getUpdatedAt()));
                return ps;
            }, keyHolder);
            comment.assignId(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(
                    "UPDATE comment SET content = ?, updated_at = ? WHERE id = ?",
                    comment.getContent(), Timestamp.valueOf(comment.getUpdatedAt()), comment.getId()
            );
        }
        return comment;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM comment WHERE id = ?", rowMapper, id)
                .stream().findFirst();
    }

    @Override
    public List<Comment> findAllByPostId(Long postId) {
        return jdbcTemplate.query("SELECT * FROM comment WHERE post_id = ?", rowMapper, postId);
    }

    @Override
    public long countByPostId(Long postId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comment WHERE post_id = ?", Long.class, postId);
        return count != null ? count : 0;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM comment WHERE id = ?", id);
    }

    @Override
    public void deleteAllByParentId(Long parentId) {
        jdbcTemplate.update("DELETE FROM comment WHERE parent_id = ?", parentId);
    }

    @Override
    public List<Comment> findAllByMemberId(Long memberId, int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
                "SELECT * FROM comment WHERE member_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?",
                rowMapper, memberId, size, offset);
    }

    @Override
    public long countByMemberId(Long memberId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comment WHERE member_id = ?", Long.class, memberId);
        return count != null ? count : 0;
    }
}
