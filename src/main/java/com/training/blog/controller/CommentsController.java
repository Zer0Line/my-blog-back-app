package com.training.blog.controller;

import com.training.blog.dto.CommentCreateRequest;
import com.training.blog.dto.CommentResponse;
import com.training.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class CommentsController {

    private final CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{id}/comments")
    public CommentResponse createComment(
            @PathVariable("id") Long postId,
            @Valid @RequestBody CommentCreateRequest request) {
        return commentService.create(postId, request);
    }

    @GetMapping("/{id}/comments")
    public List<CommentResponse> getCommentsByPostId(@PathVariable("id") Long postId) {
        return commentService.getByPostId(postId);
    }

    @GetMapping("/{id}/comments/{comment_id}")
    public CommentResponse getCommentById(
            @PathVariable("id") Long postId,
            @PathVariable("comment_id") Long commentId) {
        return commentService.getById(postId, commentId);
    }

    @PutMapping("/{id}/comments/{comment_id}")
    public CommentResponse updateComment(
            @PathVariable("id") Long postId,
            @PathVariable("comment_id") Long commentId,
            @Valid @RequestBody CommentCreateRequest request) {
        return commentService.update(postId, commentId, request);
    }

    @DeleteMapping("/{id}/comments/{comment_id}")
    public void deleteComment(
            @PathVariable("id") Long postId,
            @PathVariable("comment_id") Long commentId) {
        commentService.delete(postId, commentId);
    }
}



