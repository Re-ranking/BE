package com.example.cv_reranking.global.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.profile-prefix:profile}")
    private String profilePrefix;

    @Value("${aws.s3.cv-prefix:cv/private}")
    private String cvPrefix;

    private static final long MAX_PROFILE_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_CV_SIZE = 10 * 1024 * 1024; // 10MB

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private static final Set<String> ALLOWED_CV_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @Override
    public String saveProfileImage(String userKey, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateProfileImage(file);

        String contentType = resolveContentType(file);
        String extension = resolveExtension(file.getOriginalFilename(), contentType);
        String safeUserKey = sanitize(userKey);

        String key = profilePrefix + "/" + safeUserKey + "/" + UUID.randomUUID() + extension;

        upload(file, key, contentType);

        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build())
                .toExternalForm();
    }

    @Override
    public String saveCvPrivate(String userKey, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateCv(file);

        String contentType = resolveContentType(file);
        String extension = resolveExtension(file.getOriginalFilename(), contentType);
        String safeUserKey = sanitize(userKey);

        String key = cvPrefix + "/" + safeUserKey + "/" + UUID.randomUUID() + extension;

        upload(file, key, contentType);

        return key;
    }

    private void upload(MultipartFile file, String key, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(inputStream, file.getSize())
            );
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽는 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("S3 파일 업로드에 실패했습니다.", e);
        }
    }

    private void validateProfileImage(MultipartFile file) {
        if (file.getSize() > MAX_PROFILE_IMAGE_SIZE) {
            throw new IllegalArgumentException("프로필 이미지는 5MB 이하만 업로드할 수 있습니다.");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("프로필 이미지는 jpg, jpeg, png, webp 형식만 업로드할 수 있습니다.");
        }
    }

    private void validateCv(MultipartFile file) {
        if (file.getSize() > MAX_CV_SIZE) {
            throw new IllegalArgumentException("CV 파일은 10MB 이하만 업로드할 수 있습니다.");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_CV_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("CV는 PDF, DOC, DOCX 파일만 업로드할 수 있습니다.");
        }
    }

    private String resolveContentType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || contentType.isBlank()) {
            return "application/octet-stream";
        }

        return contentType.toLowerCase();
    }

    private String resolveExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            String extension = originalFilename
                    .substring(originalFilename.lastIndexOf("."))
                    .toLowerCase();

            if (extension.matches("\\.(jpg|jpeg|png|webp|pdf|doc|docx)$")) {
                return extension;
            }
        }

        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/jpg", "image/jpeg" -> ".jpg";
            case "application/pdf" -> ".pdf";
            case "application/msword" -> ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx";
            default -> "";
        };
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "anonymous";
        }

        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}