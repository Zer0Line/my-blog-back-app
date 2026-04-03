package com.training.blog.repository;

import com.training.blog.dto.PostResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PostResponse> findAll(String search, int offset, int limit) {
        String sql = """
                SELECT *
                FROM posts
                WHERE ? = '' OR title LIKE ? OR text LIKE ?
                ORDER BY id
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

    private List<String> parseTags(String tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return List.of(tags.split(","));
    }
}
