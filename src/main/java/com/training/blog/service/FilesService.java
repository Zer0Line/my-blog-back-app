package com.training.blog.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class FilesService {

    public static final String POST_IMAGE_DIR = "post-images/";

    public void uploadPostImage(Long postId, MultipartFile file) {
        try {
            Path imageDir = Paths.get(POST_IMAGE_DIR);
            if (!Files.exists(imageDir)) {
                Files.createDirectories(imageDir);
            }

            String extension = getFileExtension(file.getOriginalFilename());
            Path filePath = imageDir.resolve(postId + extension);
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Resource downloadPostImage(Long postId) {
        try {
            Path imageDir = Paths.get(POST_IMAGE_DIR);

            // Ищем файл с любым расширением для данного postId
            byte[] content = null;
            String[] extensions = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

            for (String ext : extensions) {
                Path filePath = imageDir.resolve(postId + ext).normalize();
                if (Files.exists(filePath)) {
                    content = Files.readAllBytes(filePath);
                    break;
                }
            }

            return Optional.ofNullable(content)
                    .map(ByteArrayResource::new)
                    .orElse(null);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String getFileExtension(String filename) {
        filename = Optional.ofNullable(filename).orElse("");
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }

    public void deletePostImage(Long postId) {
        try {
            Path imageDir = Paths.get(POST_IMAGE_DIR);
            String[] extensions = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

            for (String ext : extensions) {
                Path filePath = imageDir.resolve(postId + ext).normalize();
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
