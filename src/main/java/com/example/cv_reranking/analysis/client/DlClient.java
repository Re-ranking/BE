package com.example.cv_reranking.analysis.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DlClient {

    private final RestClient restClient = RestClient.create();

    @Value("${dl.base-url}")
    private String dlBaseUrl;

    @Value("${dl.cv-recommend-path:/recommend}")
    private String cvRecommendPath;

    public JsonNode analyzeCv(MultipartFile file) {
        try {
            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("file", new MultipartInputStreamFileResource(
                    file,
                    file.getOriginalFilename()
            ));

            return restClient.post()
                    .uri(dlBaseUrl + cvRecommendPath)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

        } catch (IOException e) {
            throw new RuntimeException("CV 파일을 DL 서버로 전송하는 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("DL 서버 분석 요청에 실패했습니다.", e);
        }
    }

    private static class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;
        private final long contentLength;

        public MultipartInputStreamFileResource(MultipartFile file, String filename) throws IOException {
            super(file.getInputStream());
            this.filename = filename;
            this.contentLength = file.getSize();
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public long contentLength() {
            return contentLength;
        }
    }
}