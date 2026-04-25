package com.training.blog.controller;

import com.training.blog.exception.MissingAttachedFileException;
import com.training.blog.service.FilesService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// import java.util.Map; // no longer needed

@RestController
@RequestMapping("/api/posts")
public class FilesController {

    private final FilesService filesService;

    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getPostImage(@PathVariable(name = "id") Long postId) {
        return filesService.downloadPostImage(postId)
                .map(payload -> ResponseEntity.ok()
                        .contentType(payload.mediaType())
                        .body((Resource) payload.resource()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadPostImage(
            @PathVariable(name = "id") Long postId,
            @RequestParam(value = "image", required = false) MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            filesService.uploadPostImage(postId, file);
        } else {
            throw new MissingAttachedFileException();
        }
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updatePostImage(
            @PathVariable(name = "id") Long postId,
            @RequestParam(value = "image", required = false) MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            filesService.uploadPostImage(postId, file);
        }
    }
}