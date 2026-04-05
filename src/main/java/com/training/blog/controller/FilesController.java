package com.training.blog.controller;

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

@RestController
@RequestMapping("/api/posts")
public class FilesController {

    private final FilesService filesService;

    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getPostImage(@PathVariable("id") Long postId) {
        Resource image = filesService.downloadPostImage(postId);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadPostImage(
            @PathVariable("id") Long postId,
            @RequestParam(value = "image", required = false) MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            filesService.uploadPostImage(postId, file);
        }
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updatePostImage(
            @PathVariable("id") Long postId,
            @RequestParam(value = "image", required = false) MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            filesService.uploadPostImage(postId, file);
        }
    }
}