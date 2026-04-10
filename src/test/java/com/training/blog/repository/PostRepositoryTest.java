package com.training.blog.repository;

import com.training.blog.config.TestConfiguration;
import com.training.blog.dto.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {TestConfiguration.class, PostRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS post_tags");
        jdbcTemplate.execute("DROP TABLE IF EXISTS comments");
        jdbcTemplate.execute("DROP TABLE IF EXISTS tags");
        jdbcTemplate.execute("DROP TABLE IF EXISTS posts");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("test-schema.sql"));
        populator.execute(dataSource);

        // Вставляем теги
        jdbcTemplate.execute("""
            INSERT INTO tags (name) VALUES
            ('tag1'), ('tag2'), ('other')
            """);

        // Вставляем посты
        jdbcTemplate.execute("""
            INSERT INTO posts (title, text, likes_count) VALUES
            ('Post 1', 'Text 1', 0),
            ('Post 2', 'Text 2', 0),
            ('Post 3', 'Text 3', 0),
            ('Post 4', 'Text 4', 0)
            """);

        // Связываем посты с тегами через post_tags
        // Post 1: tag1, tag2 | Post 2: tag1 | Post 3: tag2 | Post 4: other
        jdbcTemplate.execute("""
            INSERT INTO post_tags (post_id, tag_id) VALUES
            (1, 1),
            (1, 2),
            (2, 1),
            (3, 2),
            (4, 3)
            """);
    }

    @Test
    void findAll_withEmptyTextQueryAndTags_returnsPostsMatchingTags() {
        List<String> tags = List.of("tag1", "tag2");
        List<PostResponse> result = postRepository.findAll("", tags, 0, 10);
        assertFalse(result.isEmpty());
    }

    @Test
    void findAll_withEmptyTextQueryAndSingleTag_returnsPostsWithTag() {
        List<String> tags = List.of("tag1");
        List<PostResponse> result = postRepository.findAll("", tags, 0, 10);
        assertEquals(2, result.size());
        for (PostResponse post : result) {
            assertTrue(post.tags().contains("tag1"));
        }
    }

    @Test
    void findAll_withTextQuery_returnsMatchingPosts() {
        List<PostResponse> result = postRepository.findAll("Post 1", List.of(), 0, 10);
        assertEquals(1, result.size());
    }

    @Test
    void findAll_withTextQueryAndTags_returnsMatchingPosts() {
        List<PostResponse> result = postRepository.findAll("Text", List.of("tag1"), 0, 10);
        assertEquals(2, result.size());
    }

    @Test
    void findAll_withNoTags_returnsAllPosts() {
        List<PostResponse> result = postRepository.findAll("", List.of(), 0, 10);
        assertEquals(4, result.size());
    }

    @Test
    void findAll_withPagination_returnsCorrectLimit() {
        List<PostResponse> result = postRepository.findAll("", List.of(), 0, 2);
        assertEquals(2, result.size());
    }

    @Test
    void findAll_withOffset_returnsCorrectPosts() {
        List<PostResponse> firstPage = postRepository.findAll("", List.of(), 0, 2);
        List<PostResponse> secondPage = postRepository.findAll("", List.of(), 2, 2);
        assertEquals(2, firstPage.size());
        assertEquals(2, secondPage.size());
    }

    @Test
    void findById_existingPost_returnsPost() {
        PostResponse result = postRepository.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Post 1", result.title());
    }

    @Test
    void findById_nonExistingPost_throwsException() {
        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class,
                () -> postRepository.findById(999L));
    }

    @Test
    void create_validPost_returnsCreatedPost() {
        PostResponse result = postRepository.create("New Post", "New Text", List.of("tag1", "tag3"));
        assertEquals("New Post", result.title());
        assertEquals("New Text", result.text());
        assertTrue(result.tags().contains("tag1"));
        assertTrue(result.tags().contains("tag3"));
    }

    @Test
    void update_existingPost_returnsUpdatedPost() {
        PostResponse result = postRepository.update(1L, "Updated Title", "Updated Text", List.of("newTag"));
        assertNotNull(result);
        assertEquals("Updated Title", result.title());
        assertEquals("Updated Text", result.text());
        assertTrue(result.tags().contains("newTag"));
    }

    @Test
    void update_nonExistingPost_returnsNull() {
        PostResponse result = postRepository.update(999L, "Title", "Text", List.of());
        assertNull(result);
    }

    @Test
    void delete_existingPost_deletesSuccessfully() {
        postRepository.delete(1L);
        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class,
                () -> postRepository.findById(1L));
    }

    @Test
    void addLike_existingPost_incrementsLikesCount() {
        Long likesCount = postRepository.addLike(1L);
        assertNotNull(likesCount);
        assertEquals(1L, likesCount);
    }

    @Test
    void count_withTags_returnsCorrectCount() {
        long count = postRepository.count("", List.of("tag1"));
        assertEquals(2, count);
    }

    @Test
    void count_withTextQuery_returnsCorrectCount() {
        long count = postRepository.count("Post 1", List.of());
        assertEquals(1, count);
    }
}