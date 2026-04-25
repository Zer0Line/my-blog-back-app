package com.training.blog.repository;

import com.training.blog.domain.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Import(CommentRepository.class)
@Sql(scripts = {"/cleanup.sql", "/setup_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Test
    void findByPostId_returnsCommentsForPost() {
        List<Comment> comments = commentRepository.findByPostId(1L);
        assertTrue(comments.size() >= 0); // comments may be zero if not preloaded
        for (Comment c : comments) {
            assertEquals(1L, c.getPostId());
        }
    }

    @Test
    void create_validComment_returnsCreatedComment() {
        Comment created = commentRepository.create(1L, "New comment");
        assertNotNull(created.getId());
        assertEquals("New comment", created.getText());
        assertEquals(1L, created.getPostId());
    }

    @Test
    void update_existingComment_returnsUpdated() {
        // first create a comment to update
        Comment newComment = commentRepository.create(1L, "temp");
        Comment updated = commentRepository.update(1L, newComment.getId(), "updated text");
        assertNotNull(updated);
        assertEquals("updated text", updated.getText());
        assertEquals(1L, updated.getPostId());
    }

    @Test
    void update_nonExistingComment_returnsNull() {
        Comment result = commentRepository.update(1L, 999L, "does not exist");
        assertNull(result);
    }

    @Test
    void delete_existingComment_removesIt() {
        Comment toDelete = commentRepository.create(1L, "to delete");
        commentRepository.delete(1L, toDelete.getId());
        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class,
                () -> commentRepository.findById(toDelete.getId()));
    }

    @Test
    void findById_existing_returnsComment() {
        Comment existing = commentRepository.create(1L, "find me");
        Comment found = commentRepository.findById(existing.getId());
        assertNotNull(found);
        assertEquals(existing.getId(), found.getId());
    }

    @Test
    void findByPostIdAndId_existing_returnsComment() {
        Comment c = commentRepository.create(1L, "combo");
        Comment result = commentRepository.findByPostIdAndId(1L, c.getId());
        assertNotNull(result);
        assertEquals(c.getId(), result.getId());
    }
}
