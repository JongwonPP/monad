package com.jongwon.monad.comment.infra;

import com.jongwon.monad.comment.domain.CommentLike;
import com.jongwon.monad.comment.domain.CommentLikeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
@Profile("prod")
public class JdbcCommentLikeRepository implements CommentLikeRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<CommentLike> rowMapper = (rs, rowNum) -> CommentLike.reconstruct(
            rs.getLong("id"),
            rs.getLong("comment_id"),
            rs.getLong("member_id"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public JdbcCommentLikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CommentLike save(CommentLike commentLike) {
        if (commentLike.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO comment_like (comment_id, member_id, created_at) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setLong(1, commentLike.getCommentId());
                ps.setLong(2, commentLike.getMemberId());
                ps.setTimestamp(3, Timestamp.valueOf(commentLike.getCreatedAt()));
                return ps;
            }, keyHolder);
            commentLike.assignId(keyHolder.getKey().longValue());
        }
        return commentLike;
    }

    @Override
    public Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId) {
        return jdbcTemplate.query(
                "SELECT * FROM comment_like WHERE comment_id = ? AND member_id = ?",
                rowMapper, commentId, memberId
        ).stream().findFirst();
    }

    @Override
    public long countByCommentId(Long commentId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comment_like WHERE comment_id = ?", Long.class, commentId);
        return count != null ? count : 0;
    }

    @Override
    public void deleteByCommentIdAndMemberId(Long commentId, Long memberId) {
        jdbcTemplate.update("DELETE FROM comment_like WHERE comment_id = ? AND member_id = ?", commentId, memberId);
    }
}
