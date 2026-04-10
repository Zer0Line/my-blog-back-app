package com.training.blog.controller;

import com.training.blog.dto.Post;
import com.training.blog.dto.PostResponse;
import com.training.blog.dto.PostsPageResponse;
import com.training.blog.dto.UpdatePostRequest;
import com.training.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostsController {

    private final PostService postService;

    public PostsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public PostsPageResponse getPosts(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize
    ) {
        return postService.getPosts(search, pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    public PostResponse getPost(@PathVariable(name = "id") Long id) {
        return postService.getById(id);
    }

    @PostMapping
    public PostResponse createPost(@Valid @RequestBody Post request) {
        return postService.create(request);
    }

    @PostMapping("/{id}/likes")
    public Long addLike(@PathVariable(name = "id") Long id) {
        return postService.addLike(id);
    }

    @PutMapping("/{id}")
    public PostResponse updatePost(@PathVariable(name = "id") Long id,
                                   @Valid @RequestBody UpdatePostRequest request) {
        return postService.update(id, request.title(), request.text(), request.tags());
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable(name = "id", required = true) Long id) {
        postService.delete(id);
    }

}
