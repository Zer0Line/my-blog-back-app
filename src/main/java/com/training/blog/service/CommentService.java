package com.training.blog.service;

import com.training.blog.dto.CommentCreateRequest;
import com.training.blog.dto.CommentResponse;
import com.training.blog.mapper.CommentMapper;
import com.training.blog.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return commentMapper.toCommentResponse(commentRepository.findByPostIdAndId(postId, commentId));
    }

    @Transactional
    public CommentResponse create(Long postId, CommentCreateRequest request) {
        return commentMapper.toCommentResponse(commentRepository.create(postId, request.text()));
    }

    @Transactional
    public CommentResponse update(Long postId, Long commentId, CommentCreateRequest request) {
        return commentMapper.toCommentResponse(commentRepository.update(postId, commentId, request.text()));
    }

    @Transactional
    public void delete(Long commentId, Long postId) {
        commentRepository.delete(commentId, postId);
    }
}
