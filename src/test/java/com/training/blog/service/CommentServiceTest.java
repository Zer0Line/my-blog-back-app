package com.training.blog.service;

import com.training.blog.domain.Comment;
import com.training.blog.dto.CommentCreateRequest;
import com.training.blog.dto.CommentResponse;
import com.training.blog.mapper.CommentMapper;
import com.training.blog.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class CommentServiceTest extends BaseServiceTest {

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;

    @Test
    void contextLoads() {
        assertNotNull(commentService);
    }

    @Test
    void getByPostId_returnsResponses() {
        Comment comment = new Comment(1L, "text", 1L);
        CommentResponse resp = new CommentResponse(1L, "text", 1L);
        when(commentRepository.findByPostId(1L)).thenReturn(List.of(comment));
        when(commentMapper.toCommentResponse(comment)).thenReturn(resp);
        var result = commentService.getByPostId(1L);
        assertEquals(1, result.size());
        assertEquals("text", result.get(0).getText());
    }

    @Test
    void create_returnsResponse() {
        CommentCreateRequest req = new CommentCreateRequest("new");
        Comment comment = new Comment(2L, "new", 1L);
        CommentResponse resp = new CommentResponse(2L, "new", 1L);
        when(commentRepository.create(1L, "new")).thenReturn(comment);
        when(commentMapper.toCommentResponse(comment)).thenReturn(resp);
        var result = commentService.create(1L, req);
        assertEquals(2L, result.getId());
    }

    @Test
    void getById_missing_throwsException() {
        when(commentRepository.findByPostIdAndId(1L, 999L)).thenReturn(null);
        assertThrows(com.training.blog.exception.CommentNotFoundException.class,
                () -> commentService.getById(1L, 999L));
    }

    @Test
    void update_nonExisting_throwsException() {
        CommentCreateRequest req = new CommentCreateRequest("upd");
        when(commentRepository.update(1L, 999L, "upd")).thenReturn(null);
        assertThrows(com.training.blog.exception.CommentNotFoundException.class,
                () -> commentService.update(1L, 999L, req));
    }

    @Test
    void create_nullRequest_throwsNpe() {
        assertThrows(NullPointerException.class, () -> commentService.create(1L, null));
    }

}