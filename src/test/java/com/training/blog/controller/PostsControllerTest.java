package com.training.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.blog.dto.PostCreateRequest;
import com.training.blog.dto.PostResponse;
import com.training.blog.dto.PostsPageResponse;
import com.training.blog.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PostsController.class)
class PostsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @Test
    void getPosts() throws Exception {
        PostsPageResponse response = new PostsPageResponse(List.of(
                new PostResponse(1L, "Post 1", "Text 1", List.of("tag1"), 0L, 0L),
                new PostResponse(2L, "Post 2", "Text 2", List.of("tag2"), 0L, 0L),
                new PostResponse(3L, "Post 3", "Text 3", List.of("tag3"), 0L, 0L),
                new PostResponse(4L, "Post 4", "Text 4", List.of("other"), 0L, 0L)
        ), false, false, 1);

        when(postService.getPosts(anyString(), anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/api/posts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts.length()").value(4));
    }

    @Test
    void getPost() throws Exception {
        PostResponse response = new PostResponse(1L, "Post 1", "Text 1", List.of("tag1"), 0L, 0L);
        when(postService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/posts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Text 1"));
    }

    @Test
    void createPost() throws Exception {
        PostCreateRequest request = new PostCreateRequest("New Post", "Post content", List.of("spring"));
        PostResponse response = new PostResponse(1L, "New Post", "Post content", List.of("spring"), 0L, 0L);
        when(postService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("New Post"))
                .andExpect(jsonPath("$.text").value("Post content"))
                .andExpect(jsonPath("$.tags.length()").value(1))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));
    }

    @Test
    void addLike() throws Exception {
        when(postService.addLike(1L)).thenReturn(1L);

        mockMvc.perform(post("/api/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1L));
    }
}