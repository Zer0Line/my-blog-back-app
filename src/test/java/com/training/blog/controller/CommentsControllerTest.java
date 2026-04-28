package com.training.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.blog.dto.CommentCreateRequest;
import com.training.blog.dto.CommentResponse;
import com.training.blog.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentsController.class)
class CommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Test
    void getCommentsByPostId() throws Exception {
        List<CommentResponse> response = List.of(
                new CommentResponse(1L, "First", 1L),
                new CommentResponse(2L, "Second", 1L)
        );
        when(commentService.getByPostId(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/posts/1/comments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getCommentById() throws Exception {
        CommentResponse response = new CommentResponse(1L, "First", 1L);
        when(commentService.getById(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/posts/1/comments/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("First"));
    }

    @Test
    void createComment() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest("New comment");
        CommentResponse response = new CommentResponse(3L, "New comment", 1L);
        when(commentService.create(anyLong(), any())).thenReturn(response);

        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.text").value("New comment"));
    }

    @Test
    void updateComment() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest("Updated");
        CommentResponse response = new CommentResponse(1L, "Updated", 1L);
        when(commentService.update(anyLong(), anyLong(), any())).thenReturn(response);

        mockMvc.perform(put("/api/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated"));
    }

    @Test
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/api/posts/1/comments/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
