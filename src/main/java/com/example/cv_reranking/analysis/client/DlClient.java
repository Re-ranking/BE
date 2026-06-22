package com.example.cv_reranking.analysis.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class DlClient {

    private final RestTemplate restTemplate;

    @Value("${dl.base-url}")
    private String dlBaseUrl;

    @Value("${dl.cv-recommend-path:/recommend}")
    private String cvRecommendPath;

    public DlClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(300_000);

        this.restTemplate = new RestTemplate(factory);
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

            MediaType fileContentType = resolveContentType(file);
            String safeFilename = makeSafeFilename(file);

            log.info("[DL REQUEST] url={}", dlBaseUrl + cvRecommendPath);
            log.info("[DL REQUEST] originalFilename={}, safeFilename={}, contentType={}, size={}",
                    file.getOriginalFilename(), safeFilename, fileContentType, fileBytes.length);

            ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return safeFilename;
                }

                @Override
                public long contentLength() {
                    return fileBytes.length;
                }
            };

            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentDispositionFormData("file", safeFilename);
            fileHeaders.setContentType(fileContentType);
            fileHeaders.setContentLength(fileBytes.length);

            HttpEntity<ByteArrayResource> filePart =
                    new HttpEntity<>(fileResource, fileHeaders);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", filePart);

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            requestHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, requestHeaders);

            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    dlBaseUrl + cvRecommendPath,
                    requestEntity,
                    JsonNode.class
            );

            return response.getBody();

        } catch (IOException e) {
            throw new RuntimeException("CV 파일을 읽는 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("DL 서버 분석 요청에 실패했습니다.", e);
        }
    }

    private MediaType resolveContentType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return MediaType.parseMediaType(contentType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
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

        return "uploaded-cv" + extension;
    }
}