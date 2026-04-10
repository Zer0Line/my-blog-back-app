package com.training.blog.dto;

public record Comment(
        Long id,
        String text,
        Long postId
) {
}
