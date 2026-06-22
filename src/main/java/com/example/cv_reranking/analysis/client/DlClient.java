package com.example.cv_reranking.analysis.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DlClient {

    private final RestClient restClient = RestClient.create();

    @Value("${dl.base-url}")
    private String dlBaseUrl;

    @Value("${dl.cv-recommend-path:/recommend}")
    private String cvRecommendPath;

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
            String filename = (originalFilename == null || originalFilename.isBlank())
                    ? "cv.pdf"
                    : originalFilename;

            MediaType fileContentType = resolveContentType(file);

            log.info("[DL REQUEST] url={}", dlBaseUrl + cvRecommendPath);
            log.info("[DL REQUEST] filename={}, contentType={}, size={}",
                    filename, fileContentType, fileBytes.length);

            ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return filename;
                }

                @Override
                public long contentLength() {
                    return fileBytes.length;
                }
            };

            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentDispositionFormData("file", filename);
            fileHeaders.setContentType(fileContentType);
            fileHeaders.setContentLength(fileBytes.length);

            HttpEntity<ByteArrayResource> filePart =
                    new HttpEntity<>(fileResource, fileHeaders);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", filePart);

            return restClient.post()
                    .uri(dlBaseUrl + cvRecommendPath)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

        } catch (IOException e) {
            throw new RuntimeException("CV 파일을 읽는 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("DL 서버 분석 요청에 실패했습니다.", e);
        }
    }

    private MediaType resolveContentType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_PDF;
        }

        try {
            return MediaType.parseMediaType(contentType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}