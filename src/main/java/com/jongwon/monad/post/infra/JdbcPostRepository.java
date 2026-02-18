package com.jongwon.monad.post.infra;

import com.jongwon.monad.post.domain.Post;
import com.jongwon.monad.post.domain.PostRepository;
import com.jongwon.monad.post.domain.PostSortType;
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
public class JdbcPostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Post> rowMapper = (rs, rowNum) -> Post.reconstruct(
            rs.getLong("id"),
            rs.getLong("board_id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getLong("member_id"),
            rs.getInt("view_count"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
    );

    public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO post (board_id, title, content, member_id, view_count, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setLong(1, post.getBoardId());
                ps.setString(2, post.getTitle());
                ps.setString(3, post.getContent());
                ps.setLong(4, post.getMemberId());
                ps.setInt(5, post.getViewCount());
                ps.setTimestamp(6, Timestamp.valueOf(post.getCreatedAt()));
                ps.setTimestamp(7, Timestamp.valueOf(post.getUpdatedAt()));
                return ps;
            }, keyHolder);
            post.assignId(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(
                    "UPDATE post SET title = ?, content = ?, view_count = ?, updated_at = ? WHERE id = ?",
                    post.getTitle(), post.getContent(), post.getViewCount(),
                    Timestamp.valueOf(post.getUpdatedAt()), post.getId()
            );
        }
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM post WHERE id = ?", rowMapper, id)
                .stream().findFirst();
    }

    @Override
    public List<Post> findAllByBoardId(Long boardId, int page, int size, PostSortType sortType) {
        int offset = page * size;
        String orderBy = switch (sortType) {
            case LATEST -> "created_at DESC";
            case OLDEST -> "created_at ASC";
            case VIEWS -> "view_count DESC, created_at DESC";
            case LIKES -> "(SELECT COUNT(*) FROM post_like pl WHERE pl.post_id = p.id) DESC, p.created_at DESC";
        };
        String sql = "SELECT p.* FROM post p WHERE p.board_id = ? ORDER BY " + orderBy + " LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, rowMapper, boardId, size, offset);
    }

    @Override
    public long countByBoardId(Long boardId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post WHERE board_id = ?", Long.class, boardId);
        return count != null ? count : 0;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM post WHERE id = ?", id);
    }

    @Override
    public List<Post> searchByKeyword(String keyword, int page, int size) {
        int offset = page * size;
        String pattern = "%" + keyword + "%";
        return jdbcTemplate.query(
                "SELECT * FROM post WHERE title LIKE ? OR content LIKE ? ORDER BY created_at DESC LIMIT ? OFFSET ?",
                rowMapper, pattern, pattern, size, offset);
    }

    @Override
    public long countByKeyword(String keyword) {
        String pattern = "%" + keyword + "%";
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post WHERE title LIKE ? OR content LIKE ?", Long.class, pattern, pattern);
        return count != null ? count : 0;
    }

    @Override
    public List<Post> searchByBoardIdAndKeyword(Long boardId, String keyword, int page, int size) {
        int offset = page * size;
        String pattern = "%" + keyword + "%";
        return jdbcTemplate.query(
                "SELECT * FROM post WHERE board_id = ? AND (title LIKE ? OR content LIKE ?) ORDER BY created_at DESC LIMIT ? OFFSET ?",
                rowMapper, boardId, pattern, pattern, size, offset);
    }

    @Override
    public long countByBoardIdAndKeyword(Long boardId, String keyword) {
        String pattern = "%" + keyword + "%";
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post WHERE board_id = ? AND (title LIKE ? OR content LIKE ?)",
                Long.class, boardId, pattern, pattern);
        return count != null ? count : 0;
    }

    @Override
    public List<Post> findAllByMemberId(Long memberId, int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
                "SELECT * FROM post WHERE member_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?",
                rowMapper, memberId, size, offset);
    }

    @Override
    public long countByMemberId(Long memberId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post WHERE member_id = ?", Long.class, memberId);
        return count != null ? count : 0;
    }
}
