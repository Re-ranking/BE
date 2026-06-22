package com.example.cv_reranking.analysis.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
public class DlClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${dl.base-url}")
    private String dlBaseUrl;

    @Value("${dl.cv-recommend-path:/recommend}")
    private String cvRecommendPath;

    public DlClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public JsonNode analyzeCv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("DL로 전송할 CV 파일이 비어 있습니다.");
        }

        try {
            byte[] fileBytes = file.getBytes();

            if (fileBytes.length == 0) {
                throw new IllegalArgumentException("DL로 전송할 CV 파일 byte가 0입니다.");
            }

            String originalFilename = file.getOriginalFilename();
            String safeFilename = makeSafeFilename(file);
            String contentType = resolveContentType(file);
            String boundary = "----CvRerankingBoundary" + UUID.randomUUID();

            byte[] multipartBody = buildMultipartBody(
                    boundary,
                    "file",
                    safeFilename,
                    contentType,
                    fileBytes
            );

            String url = dlBaseUrl + cvRecommendPath;

            log.info("[DL REQUEST] url={}", url);
            log.info("[DL REQUEST] originalFilename={}, safeFilename={}, contentType={}, fileSize={}, multipartBodySize={}",
                    originalFilename, safeFilename, contentType, fileBytes.length, multipartBody.length);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMinutes(5))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            log.info("[DL RESPONSE] status={}, bodyLength={}",
                    response.statusCode(),
                    response.body() == null ? 0 : response.body().length());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(
                        "DL 서버 응답 실패. status=" + response.statusCode() + ", body=" + response.body()
                );
            }

            return objectMapper.readTree(response.body());

        } catch (IOException e) {
            throw new RuntimeException("CV 파일을 읽거나 DL 응답을 파싱하는 중 오류가 발생했습니다.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("DL 서버 요청이 중단되었습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("DL 서버 분석 요청에 실패했습니다.", e);
        }
    }

    private byte[] buildMultipartBody(
            String boundary,
            String fieldName,
            String filename,
            String contentType,
            byte[] fileBytes
    ) throws IOException {
        String lineBreak = "\r\n";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(("--" + boundary + lineBreak).getBytes(StandardCharsets.UTF_8));

        outputStream.write((
                "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + filename + "\"" + lineBreak
        ).getBytes(StandardCharsets.UTF_8));

        outputStream.write((
                "Content-Type: " + contentType + lineBreak
        ).getBytes(StandardCharsets.UTF_8));

        outputStream.write(lineBreak.getBytes(StandardCharsets.UTF_8));
        outputStream.write(fileBytes);
        outputStream.write(lineBreak.getBytes(StandardCharsets.UTF_8));

        outputStream.write(("--" + boundary + "--" + lineBreak).getBytes(StandardCharsets.UTF_8));

        return outputStream.toByteArray();
    }

    private String resolveContentType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return contentType;
    }

    private String makeSafeFilename(MultipartFile file) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        String extension = ".pdf";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        } else if ("image/png".equals(contentType)) {
            extension = ".png";
        } else if ("image/jpeg".equals(contentType) || "image/jpg".equals(contentType)) {
            extension = ".jpg";
        }

        if (!extension.matches("\\.(pdf|png|jpg|jpeg)$")) {
            extension = ".pdf";
        }

        return "uploaded-cv" + extension;
    }
}