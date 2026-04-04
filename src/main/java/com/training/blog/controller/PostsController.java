package com.training.blog.controller;

import com.training.blog.model.Post;
import com.training.blog.dto.PostResponse;
import com.training.blog.dto.PostsPageResponse;
import com.training.blog.dto.UpdatePostRequest;
import com.training.blog.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostsController {

    private final PostService postService;

    public PostsController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/api/posts")
    public PostResponse createPost(@RequestBody Post request) {
        return postService.create(request);
    }

    @PutMapping("/api/posts/{id}")
    public PostResponse updatePost(@PathVariable("id") Long id, @RequestBody UpdatePostRequest request) {
        return postService.update(id, request.title(), request.text(), request.tags());
    }

    @GetMapping("/api/posts/{id}")
    public PostResponse getPost(@PathVariable("id") Long id) {
        return postService.getById(id);
    }

    @GetMapping("/api/posts")
    public PostsPageResponse getPosts(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize
    ) {
        return postService.getPosts(search, pageNumber, pageSize);
    }

}
