package com.example.cv_reranking.global.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveProfileImage(String userKey, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateImage(file);

        String extension = getExtension(file.getOriginalFilename());
        String safeUserKey = sanitize(userKey);
        String filename = safeUserKey + "_" + UUID.randomUUID() + extension;

        Path profileDir = Path.of(uploadDir, "profile");
        save(file, profileDir, filename);

        return "/uploads/profile/" + filename;
    }

    public String saveCvPrivate(String userKey, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateCv(file);

        String extension = getExtension(file.getOriginalFilename());
        String safeUserKey = sanitize(userKey);
        String filename = safeUserKey + "_" + UUID.randomUUID() + extension;

        Path cvDir = Path.of(uploadDir, "private", "cv");
        save(file, cvDir, filename);

        return cvDir.resolve(filename).toString();
    }

    private void save(MultipartFile file, Path dir, String filename) {
        try {
            Files.createDirectories(dir);
            Path filePath = dir.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "anonymous";
        }
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }
    }

    private void validateCv(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null) {
            throw new IllegalArgumentException("파일 형식을 확인할 수 없습니다.");
        }

        boolean allowed = contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        if (!allowed) {
            throw new IllegalArgumentException("CV는 PDF, DOC, DOCX 파일만 업로드할 수 있습니다.");
        }
    }
}