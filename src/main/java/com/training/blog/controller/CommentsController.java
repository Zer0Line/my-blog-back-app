package com.training.blog.controller;

import com.training.blog.dto.CreateCommentRequest;
import com.training.blog.model.Comment;
import com.training.blog.service.CommentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class CommentsController {

    private final CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/api/posts/{id}/comments")
    public Comment createComment(@PathVariable("id") Long postId, @RequestBody CreateCommentRequest request) {
        return commentService.create(postId, request.text());
    }

    @GetMapping("/api/posts/{id}/comments")
    public List<Comment> getCommentsByPostId(@PathVariable("id") Long postId) {
        return commentService.getByPostId(postId);
    }

    @GetMapping("/api/posts/undefined/comments")
    public List<Comment> getCommentsByPostId() {
        return Collections.emptyList();
    }

    @GetMapping("/api/posts/{id}/comments/{comment_id}")
    public Comment getCommentById(@PathVariable("id") Long postId, @PathVariable("comment_id") Long commentId) {
        return commentService.getById(postId, commentId);
    }

    @PutMapping("/api/posts/{id}/comments/{comment_id}")
    public Comment updateComment(
            @PathVariable("id") Long postId,
            @PathVariable("comment_id") Long commentId,
            @RequestBody CreateCommentRequest request) {
        return commentService.update(commentId, request.text());
    }

    @DeleteMapping("/api/posts/{id}/comments/{comment_id}")
    public void deleteComment(@PathVariable("id") Long postId, @PathVariable("comment_id") Long commentId) {
        commentService.delete(commentId);
    }
}



