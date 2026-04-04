package com.training.blog.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FilesService {

    public static final String UPLOAD_DIR = "uploads/";
    public static final String POST_IMAGE_DIR = "post-images/";

    public String upload(MultipartFile file) {
        try {
            Path uploadDir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Сохраняем файл
            Path filePath = uploadDir.resolve(file.getOriginalFilename());
            file.transferTo(filePath);

            return file.getOriginalFilename();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Resource download(String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            byte[] content = Files.readAllBytes(filePath);

            return new ByteArrayResource(content);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

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

            if (content == null) {
                return null;
            }

            return new ByteArrayResource(content);
        } catch (IOException e) {
            return null;
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }

}