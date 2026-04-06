package com.training.blog.repository;


import com.training.blog.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRow, postId);
    }

    public Comment create(Long postId, String text) {
        String insertSql = "INSERT INTO comments (post_id, text) VALUES (?, ?)";

        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, postId);
            ps.setString(2, text);
            return ps;
        };

        KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbcTemplate.update(psc, keyHolder);

        Long generatedId = Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).orElseThrow();

        return findById(generatedId);
    }

    public Comment update(Long id, String text) {
        String sql = "UPDATE comments SET text = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql, text, id);

        if (updated == 0) {
            return null;
        }

        return findById(id);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteByPostId(Long postId) {
        String sql = "DELETE FROM comments WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }

    public Comment findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRow, id);
    }

    private Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Comment(
                rs.getLong("id"),
                rs.getString("text"),
                rs.getLong("post_id")
        );
    }
}