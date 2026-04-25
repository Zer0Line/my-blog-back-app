package com.training.blog.dto;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

public record ImagePayload(MediaType mediaType, ByteArrayResource resource) {
}
