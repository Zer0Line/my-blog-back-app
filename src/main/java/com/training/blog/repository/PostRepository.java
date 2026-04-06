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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PostResponse> findAll(String textQuery, List<String> tags, int offset, int limit) {
        StringBuilder sql = new StringBuilder("""
                SELECT p.id, p.title, p.text, p.tags, p.likes_count, COUNT(c.id) as comments_count
                FROM posts p
                LEFT JOIN comments c ON p.id = c.post_id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (textQuery != null && !textQuery.isEmpty()) {
            sql.append(" AND (p.title LIKE ? OR p.text LIKE ?)");
            String searchPattern = "%" + textQuery + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Фильтрация тегов по "И"
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                sql.append(" AND p.tags LIKE ?");
                params.add("%" + tag + "%");
            }
        }

        sql.append("""
                GROUP BY p.id
                ORDER BY p.id
                LIMIT ? OFFSET ?
                """);

        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new PostResponse(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("text"),
                parseTags(rs.getString("tags")),
                rs.getLong("likes_count"),
                rs.getLong("comments_count")
        ), params.toArray());
    }

    public long count(String textQuery, List<String> tags) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM posts WHERE 1=1");

        List<Object> params = new java.util.ArrayList<>();

        if (textQuery != null && !textQuery.isEmpty()) {
            sql.append(" AND (title LIKE ? OR text LIKE ?)");
            String searchPattern = "%" + textQuery + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                sql.append(" AND tags LIKE ?");
                params.add("%" + tag + "%");
            }
        }

        return Optional.ofNullable(
                        jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray()))
                .orElse(0L);
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

        Long generatedId = Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).orElseThrow();

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
