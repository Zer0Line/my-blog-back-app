package com.training.blog.dto;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImagePayload {
    private final MediaType mediaType;
    private final ByteArrayResource resource;
}
