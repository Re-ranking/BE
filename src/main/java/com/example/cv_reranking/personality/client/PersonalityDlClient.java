package com.example.cv_reranking.personality.client;

import com.example.cv_reranking.personality.dto.PersonalitySurveyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PersonalityDlClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${dl.base-url}")
    private String dlBaseUrl;

    @Value("${dl.personality-path:/personality/train}")
    private String personalityPath;

    public void sendPersonalitySurvey(PersonalitySurveyResponse response) {
        String url = dlBaseUrl + personalityPath;
        ResponseEntity<Void> dlResponse = restTemplate.postForEntity(url, response, Void.class);

        if (!dlResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("DL 성향 데이터 전달 실패");
        }
    }
}