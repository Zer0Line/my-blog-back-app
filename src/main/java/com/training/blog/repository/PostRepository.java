package com.training.blog.repository;

import com.training.blog.dto.PostResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
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
                SELECT DISTINCT p.id, p.title, p.text, p.likes_count, COUNT(DISTINCT c.id) as comments_count
                FROM posts p
                LEFT JOIN comments c ON p.id = c.post_id
                LEFT JOIN post_tags pt ON p.id = pt.post_id
                LEFT JOIN tags t ON pt.tag_id = t.id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (textQuery != null && !textQuery.isEmpty()) {
            sql.append(" AND (p.title LIKE ? OR p.text LIKE ?)");
            String searchPattern = "%" + textQuery + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Фильтрация тегов по "И" (должны быть все указанные теги)
        if (tags != null && !tags.isEmpty()) {
            sql.append(" AND p.id IN (");
            sql.append("SELECT pt2.post_id FROM post_tags pt2 ");
            sql.append("JOIN tags t2 ON pt2.tag_id = t2.id ");
            sql.append("WHERE t2.name IN (");
            for (int i = 0; i < tags.size(); i++) {
                sql.append(i > 0 ? ",?" : "?");
                params.add(tags.get(i));
            }
            sql.append(") GROUP BY pt2.post_id ");
            sql.append("HAVING COUNT(DISTINCT t2.name) = ?");
            params.add(tags.size());
            sql.append(")");
        }

        sql.append("""
                GROUP BY p.id
                ORDER BY p.id
                LIMIT ? OFFSET ?
                """);

        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            Long postId = rs.getLong("id");
            List<String> postTags = findTagsByPostId(postId);
            return new PostResponse(
                    postId,
                    rs.getString("title"),
                    rs.getString("text"),
                    postTags,
                    rs.getLong("likes_count"),
                    rs.getLong("comments_count")
            );
        }, params.toArray());
    }

    public long count(String textQuery, List<String> tags) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(DISTINCT p.id) FROM posts p
                LEFT JOIN post_tags pt ON p.id = pt.post_id
                LEFT JOIN tags t ON pt.tag_id = t.id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (textQuery != null && !textQuery.isEmpty()) {
            sql.append(" AND (p.title LIKE ? OR p.text LIKE ?)");
            String searchPattern = "%" + textQuery + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                sql.append(" AND t.name = ?");
                params.add(tag);
            }
        }

        return Optional.ofNullable(
                        jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray()))
                .orElse(0L);
    }

    public PostResponse create(String title, String text, List<String> tags) {
        String insertSql = "INSERT INTO posts (title, text, likes_count) VALUES (?, ?, 0)";

        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, text);
            return ps;
        };

        KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbcTemplate.update(psc, keyHolder);

        Long generatedId = Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).orElseThrow();

        // Добавляем теги
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                Long tagId = getOrCreateTag(tag);
                jdbcTemplate.update("INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)",
                        generatedId, tagId);
            }
        }

        return findById(generatedId);
    }

    public PostResponse update(Long id, String title, String text, List<String> tags) {
        String sql = "UPDATE posts SET title = ?, text = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql, title, text, id);

        if (updated == 0) {
            return null;
        }

        // Удаляем старые теги и добавляем новые
        jdbcTemplate.update("DELETE FROM post_tags WHERE post_id = ?", id);

        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                Long tagId = getOrCreateTag(tag);
                jdbcTemplate.update("INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)",
                        id, tagId);
            }
        }

        return findById(id);
    }

    public PostResponse findById(Long id) {
        String sql = """
                SELECT p.id, p.title, p.text, p.likes_count, COUNT(DISTINCT c.id) as comments_count
                FROM posts p
                LEFT JOIN comments c ON p.id = c.post_id
                WHERE p.id = ?
                GROUP BY p.id
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            List<String> postTags = findTagsByPostId(id);
            return new PostResponse(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("text"),
                    postTags,
                    rs.getLong("likes_count"),
                    rs.getLong("comments_count")
            );
        }, id);
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

    private List<String> findTagsByPostId(Long postId) {
        String sql = """
                SELECT t.name FROM tags t
                JOIN post_tags pt ON t.id = pt.tag_id
                WHERE pt.post_id = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("name"), postId);
    }

    private Long getOrCreateTag(String tagName) {
        String findSql = "SELECT id FROM tags WHERE name = ?";
        List<Long> existing = jdbcTemplate.query(findSql, (rs, rowNum) -> rs.getLong("id"), tagName);

        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        // Создаём новый тег
        String insertSql = "INSERT INTO tags (name) VALUES (?)";
        KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tagName);
            return ps;
        }, keyHolder);

        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).orElseThrow();
    }
}