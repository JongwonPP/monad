package com.jongwon.monad.post.infra;

import com.jongwon.monad.post.domain.PostLike;
import com.jongwon.monad.post.domain.PostLikeRepository;
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
public class JdbcPostLikeRepository implements PostLikeRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<PostLike> rowMapper = (rs, rowNum) -> PostLike.reconstruct(
            rs.getLong("id"),
            rs.getLong("post_id"),
            rs.getLong("member_id"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public JdbcPostLikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PostLike save(PostLike postLike) {
        if (postLike.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO post_like (post_id, member_id, created_at) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setLong(1, postLike.getPostId());
                ps.setLong(2, postLike.getMemberId());
                ps.setTimestamp(3, Timestamp.valueOf(postLike.getCreatedAt()));
                return ps;
            }, keyHolder);
            postLike.assignId(keyHolder.getKey().longValue());
        }
        return postLike;
    }

    @Override
    public Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId) {
        return jdbcTemplate.query(
                "SELECT * FROM post_like WHERE post_id = ? AND member_id = ?",
                rowMapper, postId, memberId
        ).stream().findFirst();
    }

    @Override
    public long countByPostId(Long postId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post_like WHERE post_id = ?", Long.class, postId);
        return count != null ? count : 0;
    }

    @Override
    public void deleteByPostIdAndMemberId(Long postId, Long memberId) {
        jdbcTemplate.update("DELETE FROM post_like WHERE post_id = ? AND member_id = ?", postId, memberId);
    }
}
