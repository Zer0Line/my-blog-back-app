package com.training.blog.repository;

import com.training.blog.dto.PostResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PostResponse> findAll(String search, int offset, int limit) {
        String sql = """
                SELECT p.id, p.title, p.text, p.tags, p.likes_count, COUNT(c.id) as comments_count
                FROM posts p
                LEFT JOIN comments c ON p.id = c.post_id
                WHERE ? = '' OR p.title LIKE ? OR p.text LIKE ?
                GROUP BY p.id
                ORDER BY p.id
                LIMIT ? OFFSET ?
                """;
        String searchPattern = "%" + search + "%";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new PostResponse(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("text"),
                parseTags(rs.getString("tags")),
                rs.getLong("likes_count"),
                rs.getLong("comments_count")
        ), search, searchPattern, search, limit, offset);
    }

    public long count(String search) {
        String sql = "SELECT COUNT(*) FROM posts WHERE ? = '' OR title LIKE ? OR text LIKE ?";
        String searchPattern = "%" + search + "%";
        return jdbcTemplate.queryForObject(sql, Long.class, search, searchPattern, searchPattern);
    }

    public PostResponse create(String title, String text, List<String> tags) {
        String tagsString = tags != null ? String.join(",", tags) : "";

        String insertSql = "INSERT INTO posts (title, text, tags, likes_count) VALUES (?, ?, ?,0)";

        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, text);
            ps.setString(3, tagsString);
            return ps;
        };

        KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbcTemplate.update(psc, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        if (generatedId == null) {
            throw new RuntimeException("Failed to get generated id");
        }

        return findById(generatedId);
    }

    public PostResponse update(Long id, String title, String text, List<String> tags) {
        String tagsString = tags != null ? String.join(",", tags) : "";

        String sql = "UPDATE posts SET title = ?, text = ?, tags = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql, title, text, tagsString, id);

        if (updated == 0) {
            return null;
        }

        return findById(id);
    }

    public PostResponse findById(Long id) {
        String sql = """
                SELECT p.id, p.title, p.text, p.tags, p.likes_count, COUNT(c.id) as comments_count
                FROM posts p
                LEFT JOIN comments c ON p.id = c.post_id
                WHERE p.id = ?
                GROUP BY p.id
                """;
        return jdbcTemplate.queryForObject(sql, this::mapRow, id);
    }

    public Long addLike(Long id) {
        String sql = "UPDATE posts SET likes_count = likes_count +1 WHERE id = ?";
        int updated = jdbcTemplate.update(sql, id);

        if (updated == 0) {
            return null;
        }

 String count = "SELECT likes_count FROM posts WHERE id = ?";
 return jdbcTemplate.queryForObject(count, Long.class, id);
 }

 public void delete(Long id) {
 String sql = "DELETE FROM posts WHERE id = ?";
 jdbcTemplate.update(sql, id);
 }

 private PostResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PostResponse(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("text"),
                parseTags(rs.getString("tags")),
                rs.getLong("likes_count"),
                rs.getLong("comments_count")
        );
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return List.of(tags.split(","));
    }
}
