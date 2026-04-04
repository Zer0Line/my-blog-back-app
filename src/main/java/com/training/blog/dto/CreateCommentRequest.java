package com.training.blog.dto;

public record CreateCommentRequest(
 String text,
 Long postId
) {
}
