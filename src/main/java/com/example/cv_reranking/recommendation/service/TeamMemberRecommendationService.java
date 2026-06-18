package com.example.cv_reranking.recommendation.service;

import com.example.cv_reranking.auth.entity.Member;
import com.example.cv_reranking.auth.repository.MemberRepository;
import com.example.cv_reranking.personality.client.PersonalityDlClient;
import com.example.cv_reranking.personality.dto.PersonalitySurveyResponse;
import com.example.cv_reranking.personality.entity.PersonalitySurvey;
import com.example.cv_reranking.personality.repository.PersonalitySurveyRepository;
import com.example.cv_reranking.recommendation.dto.TeamMemberRecommendationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberRecommendationService {

    private final MemberRepository memberRepository;
    private final PersonalitySurveyRepository personalitySurveyRepository;
    private final PersonalityDlClient personalityDlClient;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<TeamMemberRecommendationResponse> recommendTeamMembers(String cognitoSub) {
        Member member = memberRepository.findByCognitoSub(cognitoSub)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        PersonalitySurvey survey = personalitySurveyRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("저장된 성향 설문이 없습니다."));

        PersonalitySurveyResponse surveyResponse =
                PersonalitySurveyResponse.from(survey, objectMapper);

        JsonNode mlResponse = personalityDlClient.recommendTeamMembers(surveyResponse);

        JsonNode data = mlResponse.has("data") ? mlResponse.path("data") : mlResponse;
        JsonNode teammates = data.path("recommended_teammates");

        if (!teammates.isArray()) {
            throw new IllegalStateException("ML 팀원 추천 결과 형식이 올바르지 않습니다.");
        }

        List<TeamMemberRecommendationResponse> result = new ArrayList<>();

        for (JsonNode item : teammates) {
            List<String> skills = toStringList(item.path("candidate_skills"));
            List<String> domains = toStringList(item.path("candidate_domains"));

            result.add(new TeamMemberRecommendationResponse(
                    item.path("rank").asInt(),
                    item.path("candidate_user_id").asText(),
                    item.path("candidate_name").asText(),
                    inferRole(skills, domains),
                    (int) Math.round(item.path("final_score").asDouble() * 100),
                    skills,
                    domains,
                    item.path("reason").asText()
            ));
        }

        return result;
    }

    private List<String> toStringList(JsonNode node) {
        List<String> result = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode item : node) {
                result.add(item.asText());
            }
        }

        return result;
    }

    private String inferRole(List<String> skills, List<String> domains) {
        String text = String.join(" ", skills) + " " + String.join(" ", domains);

        if (text.contains("Backend") || text.contains("Java") || text.contains("Spring")) {
            return "BACKEND";
        }

        if (text.contains("Frontend") || text.contains("React")) {
            return "FRONTEND";
        }

        if (text.contains("Design") || text.contains("UI") || text.contains("UX")) {
            return "DESIGN";
        }

        if (text.contains("Data") || text.contains("Python") || text.contains("Machine Learning")) {
            return "DATA";
        }

        return "FULLSTACK";
    }
}