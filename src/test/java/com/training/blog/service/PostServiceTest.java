package com.training.blog.service;

import com.training.blog.domain.Post;
import com.training.blog.dto.PostResponse;
import com.training.blog.dto.PostsPageResponse;
import com.training.blog.dto.UpdatePostRequest;
import com.training.blog.mapper.PostMapper;
import com.training.blog.repository.CommentRepository;
import com.training.blog.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class PostServiceTest extends BaseServiceTest {

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private FilesService filesService;

    @MockitoBean
    private PostMapper postMapper;


    @Autowired
    private PostService postService;

    @Test
    void contextLoads() {
        assertNotNull(postService);
    }

    @Test
    void getById_existing_returnsResponse() {
        Post post = new Post(1L, "Title", "Text", List.of("tag"));
        PostResponse resp = new PostResponse(1L, "Title", "Text", List.of("tag"), 0L, 0L);
        when(postRepository.findById(1L)).thenReturn(post);
        when(postMapper.toPostResponse(post)).thenReturn(resp);
        PostResponse result = postService.getById(1L);
        assertEquals("Title", result.title());
    }

    @Test
    void getById_missing_throwsException() {
        when(postRepository.findById(999L)).thenReturn(null);
        assertThrows(com.training.blog.exception.PostNotFoundException.class,
                () -> postService.getById(999L));
    }

    @Test
    void addLike_success() {
        when(postRepository.addLike(1L)).thenReturn(1L);
        Long likes = postService.addLike(1L);
        assertEquals(1L, likes);
    }

    @Test
    void getPosts_returnsPage() {
        Post post = new Post(1L, "Title", "Text", List.of("tag"));
        PostResponse resp = new PostResponse(1L, "Title", "Text", List.of("tag"), 0L, 0L);
        when(postRepository.findAll(anyString(), anyList(), anyInt(), anyInt()))
                .thenReturn(List.of(post));
        when(postMapper.toPostResponse(post)).thenReturn(resp);
        when(postRepository.count(anyString(), anyList())).thenReturn(1L);
        PostsPageResponse page = postService.getPosts("", 1, 10);
        assertEquals(1, page.posts().size());
    }

    @Test
    void getPosts_emptyResult_returnsEmptyPage() {
        when(postRepository.findAll(anyString(), anyList(), anyInt(), anyInt()))
                .thenReturn(List.of());
        when(postRepository.count(anyString(), anyList())).thenReturn(0L);
        PostsPageResponse page = postService.getPosts("nonexistent", 1, 10);
        assertTrue(page.posts().isEmpty());
        assertEquals(0, page.lastPage());
    }

    @Test
    void update_nonExisting_throwsException() {
        UpdatePostRequest req = new UpdatePostRequest("New", "New", List.of());
        when(postMapper.toPost(any(UpdatePostRequest.class))).thenReturn(new Post());
        when(postRepository.update(any())).thenReturn(null);
        assertThrows(com.training.blog.exception.PostNotFoundException.class,
                () -> postService.update(999L, req));
    }

    @Test
    void addLike_nonExisting_throwsException() {
        when(postRepository.addLike(999L)).thenReturn(null);
        assertThrows(com.training.blog.exception.PostNotFoundException.class,
                () -> postService.addLike(999L));
    }

}