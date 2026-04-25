package com.training.blog.repository;

import com.training.blog.domain.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@JdbcTest
@Import(PostRepository.class)
@Sql(scripts = {"/cleanup.sql", "/setup_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Test
    void findAll_withEmptyTextQueryAndTags_returnsPostsMatchingTags() {
        List<String> tags = List.of("tag1", "tag2");
        List<Post> result = postRepository.findAll("", tags, 0, 10);
        assertFalse(result.isEmpty());
    }

    @Test
    void findAll_withEmptyTextQueryAndSingleTag_returnsPostsWithTag() {
        List<String> tags = List.of("tag1");
        List<Post> result = postRepository.findAll("", tags, 0, 10);
        assertEquals(2, result.size());
        for (Post post : result) {
            assertTrue(post.getTags().contains("tag1"));
        }
    }

    @Test
    void findAll_withTextQuery_returnsMatchingPosts() {
        List<Post> result = postRepository.findAll("Post 1", List.of(), 0, 10);
        assertEquals(1, result.size());
    }

    @Test
    void findAll_withTextQueryAndTags_returnsMatchingPosts() {
        List<Post> result = postRepository.findAll("Text", List.of("tag1"), 0, 10);
        assertEquals(2, result.size());
    }

    @Test
    void findAll_withNoTags_returnsAllPosts() {
        List<Post> result = postRepository.findAll("", List.of(), 0, 10);
        assertEquals(4, result.size());
    }

    @Test
    void findAll_withPagination_returnsCorrectLimit() {
        List<Post> result = postRepository.findAll("", List.of(), 0, 2);
        assertEquals(2, result.size());
    }

    @Test
    void findAll_withOffset_returnsCorrectPosts() {
        List<Post> firstPage = postRepository.findAll("", List.of(), 0, 2);
        List<Post> secondPage = postRepository.findAll("", List.of(), 2, 2);
        assertEquals(2, firstPage.size());
        assertEquals(2, secondPage.size());
    }

    @Test
    void findById_existingPost_returnsPost() {
        Post result = postRepository.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Post 1", result.getTitle());
    }

    @Test
    void findById_nonExistingPost_throwsException() {
        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class,
                () -> postRepository.findById(999L));
    }

    @Test
    void create_validPost_returnsCreatedPost() {
        Post testPost = new Post("New Post", "New Text", List.of("tag1", "tag3"));
        Post result = postRepository.create(testPost);
        assertEquals("New Post", result.getTitle());
        assertEquals("New Text", result.getText());
        assertTrue(result.getTags().contains("tag1"));
        assertTrue(result.getTags().contains("tag3"));
    }

    @Test
    void update_existingPost_returnsUpdatedPost() {
        Post testPost = new Post(1L, "Updated Title", "Updated Text", List.of("newTag"));
        Post result = postRepository.update(testPost);
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Text", result.getText());
        assertTrue(result.getTags().contains("newTag"));
    }

    @Test
    void update_nonExistingPost_returnsNull() {
        Post testPost = new Post(999L, "Title", "Text", List.of());
        Post result = postRepository.update(testPost);
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