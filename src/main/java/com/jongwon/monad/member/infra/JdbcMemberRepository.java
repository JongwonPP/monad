package com.jongwon.monad.member.infra;

import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
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
public class JdbcMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Member> rowMapper = (rs, rowNum) -> Member.reconstruct(
            rs.getLong("id"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("nickname"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
    );

    public JdbcMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Member save(Member member) {
        if (member.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO member (email, password, nickname, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, member.getEmail());
                ps.setString(2, member.getPassword());
                ps.setString(3, member.getNickname());
                ps.setTimestamp(4, Timestamp.valueOf(member.getCreatedAt()));
                ps.setTimestamp(5, Timestamp.valueOf(member.getUpdatedAt()));
                return ps;
            }, keyHolder);
            member.assignId(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(
                    "UPDATE member SET email = ?, password = ?, nickname = ?, updated_at = ? WHERE id = ?",
                    member.getEmail(), member.getPassword(), member.getNickname(),
                    Timestamp.valueOf(member.getUpdatedAt()), member.getId()
            );
        }
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM member WHERE id = ?", rowMapper, id)
                .stream().findFirst();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return jdbcTemplate.query("SELECT * FROM member WHERE email = ?", rowMapper, email)
                .stream().findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM member WHERE email = ?", Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByNickname(String nickname) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM member WHERE nickname = ?", Integer.class, nickname);
        return count != null && count > 0;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM member WHERE id = ?", id);
    }
}
