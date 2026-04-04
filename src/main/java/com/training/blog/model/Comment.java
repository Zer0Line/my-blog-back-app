package com.training.blog.model;

public record Comment(
        Long id,
        String text,
        Long postId
) {
}
