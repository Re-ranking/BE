package com.example.cv_reranking.analysis.service;

import com.example.cv_reranking.analysis.client.DlClient;
import com.example.cv_reranking.analysis.dto.CvAnalyzeResponse;
import com.example.cv_reranking.competition.entity.Competition;
import com.example.cv_reranking.competition.repository.CompetitionRepository;
import com.example.cv_reranking.recommendation.service.CompetitionRecommendationService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CvAnalysisService {

    private final DlClient dlClient;
    private final CompetitionRepository competitionRepository;
    private final CompetitionRecommendationService recommendationService;

    public CvAnalyzeResponse analyzeCv(MultipartFile file, String loginUserId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("분석할 CV 파일이 필요합니다.");
        }

        JsonNode dlResult = dlClient.analyzeCv(file);

        if (dlResult == null || !dlResult.isArray() || dlResult.isEmpty()) {
            throw new IllegalStateException("DL 분석 결과가 비어 있습니다.");
        }

        JsonNode firstUser = dlResult.get(0);
        JsonNode recommendationNodes = firstUser.path("recommendations");

        String userId = firstUser.path("user_id").asText();

        List<String> skills = extractFirstRecommendationArray(recommendationNodes, "cv_skills");
        List<String> domains = extractFirstRecommendationArray(recommendationNodes, "cv_domains");

        CvAnalyzeResponse.CvAnalysis cvAnalysis = new CvAnalyzeResponse.CvAnalysis(
                makeSummary(domains, skills),
                skills,
                domains,
                makeStrengths(skills, domains),
                makeWeaknesses(skills)
        );

        List<CvAnalyzeResponse.RecommendedCompetition> recommendations =
                makeRecommendations(recommendationNodes);

        recommendationService.replaceRecommendations(loginUserId, recommendations);

        return new CvAnalyzeResponse(
                loginUserId,
                firstUser.path("name").asText(),
                cvAnalysis,
                recommendations
        );
    }

    private List<CvAnalyzeResponse.RecommendedCompetition> makeRecommendations(JsonNode recommendationNodes) {
        List<CvAnalyzeResponse.RecommendedCompetition> recommendations = new ArrayList<>();

        if (recommendationNodes == null || !recommendationNodes.isArray()) {
            return recommendations;
        }

        recommendationNodes.forEach(node -> {
            Long dlContestId = node.path("contest_id").asLong();

            Competition competition = competitionRepository.findByDlContestId(dlContestId)
                    .orElse(null);

            recommendations.add(new CvAnalyzeResponse.RecommendedCompetition(
                    competition != null ? competition.getId() : null,
                    dlContestId,
                    competition != null ? competition.getName() : node.path("title").asText(),
                    toPercent(node.path("final_score").asDouble()),
                    toPercent(node.path("domain_score").asDouble()),
                    toPercent(node.path("skill_score").asDouble()),
                    competition != null ? competition.getCategory() : "",
                    competition != null ? competition.getApplicationTarget() : "",
                    competition != null ? competition.getOrganizer() : "",
                    competition != null ? competition.getApplicationPeriod() : "",
                    competition != null ? competition.getRepresentativeImageUrl() : ""
            ));
        });

        return recommendations;
    }

    private List<String> extractFirstRecommendationArray(JsonNode recommendationNodes, String fieldName) {
        List<String> values = new ArrayList<>();

        if (recommendationNodes == null || !recommendationNodes.isArray() || recommendationNodes.isEmpty()) {
            return values;
        }

        recommendationNodes.get(0).path(fieldName)
                .forEach(value -> values.add(value.asText()));

        return values;
    }

    private String makeSummary(List<String> domains, List<String> skills) {
        String domainText = domains.isEmpty() ? "기술" : String.join(", ", domains);
        String skillText = skills.isEmpty() ? "핵심 역량" : String.join(", ", skills);

        return domainText + " 분야를 중심으로 " + skillText + " 역량을 보유한 지원자입니다.";
    }

    private List<CvAnalyzeResponse.AnalysisScore> makeStrengths(List<String> skills, List<String> domains) {
        int technicalScore = Math.min(95, 45 + skills.size() * 10);
        int problemSolvingScore = Math.min(90, 35 + domains.size() * 12);
        int projectScore = skills.contains("Python") || skills.contains("Java") ? 65 : 45;
        int communicationScore = 40;

        return List.of(
                new CvAnalyzeResponse.AnalysisScore("기술적 전문성", technicalScore),
                new CvAnalyzeResponse.AnalysisScore("문제 해결력", problemSolvingScore),
                new CvAnalyzeResponse.AnalysisScore("프로젝트 관리", projectScore),
                new CvAnalyzeResponse.AnalysisScore("커뮤니케이션", communicationScore)
        );
    }

    private List<CvAnalyzeResponse.AnalysisScore> makeWeaknesses(List<String> skills) {
        List<CvAnalyzeResponse.AnalysisScore> weaknesses = new ArrayList<>();

        if (!skills.contains("AI") && !skills.contains("Machine Learning") && !skills.contains("Deep Learning")) {
            weaknesses.add(new CvAnalyzeResponse.AnalysisScore("AI 실무 경험", 35));
        }

        if (!skills.contains("React") && !skills.contains("Spring Boot")) {
            weaknesses.add(new CvAnalyzeResponse.AnalysisScore("협업 및 영향력", 40));
        }

        weaknesses.add(new CvAnalyzeResponse.AnalysisScore("발표", 45));
        weaknesses.add(new CvAnalyzeResponse.AnalysisScore("원격 협업", 42));

        return weaknesses;
    }

    private int toPercent(double score) {
        return (int) Math.round(score * 100);
    }
}