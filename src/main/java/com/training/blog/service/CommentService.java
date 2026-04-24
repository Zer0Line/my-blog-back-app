package com.training.blog.service;

import com.training.blog.dto.Comment;
import com.training.blog.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment getById(Long postId, Long commentId) {
        return commentRepository.findByPostIdAndId(postId, commentId);
    }

    @Transactional
    public Comment create(Long postId, String text) {
        return commentRepository.create(postId, text);
    }

    @Transactional
    public Comment update(Long postId, Long commentId, String text) {
        return commentRepository.update(postId, commentId, text);
    }

    @Transactional
    public void delete(Long commentId, Long postId) {
        commentRepository.delete(commentId, postId);
    }
}
