package com.training.blog.model;

import java.util.List;

public record Post(
        String title,
        String text,
        List<String> tags) {
}
