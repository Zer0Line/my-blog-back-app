package com.training.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Comment(
        Long id,

        @NotBlank(message = "Comment text is required")
        String text,

        @NotNull(message = "Post ID is required")
        Long postId
) {
}
