package com.training.blog.service;

import com.training.blog.dto.CommentCreateRequest;
import com.training.blog.dto.CommentResponse;
import com.training.blog.exception.CommentNotFoundException;
import com.training.blog.mapper.CommentMapper;
import com.training.blog.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public List<CommentResponse> getByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(commentMapper::toCommentResponse)
                .toList();
    }

    public CommentResponse getById(Long postId, Long commentId) {
        return Optional.ofNullable(commentRepository.findByPostIdAndId(postId, commentId))
                .map(commentMapper::toCommentResponse)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    @Transactional
    public CommentResponse create(Long postId, CommentCreateRequest request) {
        return commentMapper.toCommentResponse(commentRepository.create(postId, request.text()));
    }

    @Transactional
    public CommentResponse update(Long postId, Long commentId, CommentCreateRequest request) {
        return Optional.ofNullable(commentRepository.update(postId, commentId, request.text()))
                .map(commentMapper::toCommentResponse)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    @Transactional
    public void delete(Long commentId, Long postId) {
        commentRepository.delete(commentId, postId);
    }
}
