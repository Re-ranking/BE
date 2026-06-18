package com.example.cv_reranking.personality.client;

import com.example.cv_reranking.personality.dto.PersonalitySurveyResponse;
import com.fasterxml.jackson.databind.JsonNode;
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

    @Value("${dl.personality-train-path:/personality/train}")
    private String personalityTrainPath;

    @Value("${dl.personality-recommend-path:/personality/recommend}")
    private String personalityRecommendPath;

    public void sendPersonalitySurvey(PersonalitySurveyResponse response) {
        String url = dlBaseUrl + personalityTrainPath;
        ResponseEntity<Void> dlResponse = restTemplate.postForEntity(url, response, Void.class);

        if (!dlResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("DL 성향 데이터 전달 실패");
        }
    }

    public JsonNode recommendTeamMembers(PersonalitySurveyResponse response) {
        String url = dlBaseUrl + personalityRecommendPath;
        ResponseEntity<JsonNode> dlResponse = restTemplate.postForEntity(url, response, JsonNode.class);

        if (!dlResponse.getStatusCode().is2xxSuccessful() || dlResponse.getBody() == null) {
            throw new IllegalStateException("DL 팀원 추천 요청 실패");
        }

        return dlResponse.getBody();
    }
}