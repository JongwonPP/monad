package com.jongwon.monad.board.infra;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;
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
public class JdbcBoardRepository implements BoardRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Board> rowMapper = (rs, rowNum) -> Board.reconstruct(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
    );

    public JdbcBoardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Board save(Board board) {
        if (board.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO board (name, description, created_at, updated_at) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, board.getName());
                ps.setString(2, board.getDescription());
                ps.setTimestamp(3, Timestamp.valueOf(board.getCreatedAt()));
                ps.setTimestamp(4, Timestamp.valueOf(board.getUpdatedAt()));
                return ps;
            }, keyHolder);
            board.assignId(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(
                    "UPDATE board SET name = ?, description = ?, updated_at = ? WHERE id = ?",
                    board.getName(), board.getDescription(),
                    Timestamp.valueOf(board.getUpdatedAt()), board.getId()
            );
        }
        return board;
    }

    @Override
    public Optional<Board> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM board WHERE id = ?", rowMapper, id)
                .stream().findFirst();
    }

    @Override
    public List<Board> findAll() {
        return jdbcTemplate.query("SELECT * FROM board", rowMapper);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM board WHERE id = ?", id);
    }

    @Override
    public boolean existsByName(String name) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM board WHERE name = ?", Integer.class, name);
        return count != null && count > 0;
    }
}
