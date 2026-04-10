package com.training.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest(
        @NotBlank(message = "Comment text is required")
        String text,

        @NotNull(message="Post ID must be provided")
        Long postId
) {
}
