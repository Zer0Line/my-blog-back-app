package com.training.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdatePostRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @NotBlank(message = "Text is required")
        @Size(max = 10000, message = "Text must not exceed 10000 characters")
        String text,

        List<String> tags) {
}
