package com.example.cv_reranking.analysis.service;

import com.example.cv_reranking.analysis.client.DlClient;
import com.example.cv_reranking.analysis.dto.CvAnalyzeResponse;
import com.example.cv_reranking.analysis.entity.CvAnalysisRecord;
import com.example.cv_reranking.analysis.entity.CvAnalysisScoreItem;
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
    private final CvAnalysisStorageService cvAnalysisStorageService;

    public CvAnalyzeResponse analyzeCv(MultipartFile file, String loginUserId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("분석할 CV 파일이 필요합니다.");
        }

        JsonNode dlResult = dlClient.analyzeCv(file);
        JsonNode payload = unwrapDlData(dlResult);
        JsonNode firstUser = resolveFirstUser(payload);
        JsonNode recommendationNodes = resolveRecommendationNodes(payload, firstUser);

        List<String> skills = firstNonEmpty(
                extractStringArray(firstUser, "cv_skills", "cvSkills", "skills"),
                extractFirstRecommendationArray(recommendationNodes, "cv_skills", "cvSkills", "skills")
        );

        List<String> domains = firstNonEmpty(
                extractStringArray(firstUser, "cv_domains", "cvDomains", "primaryDomains", "domains"),
                extractFirstRecommendationArray(recommendationNodes, "cv_domains", "cvDomains", "primaryDomains", "domains")
        );

        List<CvAnalyzeResponse.AnalysisScore> strengths = firstNonEmptyScores(
                extractAnalysisScores(
                        firstUser,
                        "strengths",
                        "cv_strengths",
                        "cvStrengths",
                        "strongPoints",
                        "strong_points"
                ),
                extractFirstRecommendationScores(
                        recommendationNodes,
                        "strengths",
                        "cv_strengths",
                        "cvStrengths",
                        "strongPoints",
                        "strong_points"
                )
        );

        List<CvAnalyzeResponse.AnalysisScore> weaknesses = firstNonEmptyScores(
                extractAnalysisScores(
                        firstUser,
                        "weaknesses",
                        "cv_weaknesses",
                        "cvWeaknesses",
                        "weakPoints",
                        "weak_points"
                ),
                extractFirstRecommendationScores(
                        recommendationNodes,
                        "weaknesses",
                        "cv_weaknesses",
                        "cvWeaknesses",
                        "weakPoints",
                        "weak_points"
                )
        );

        CvAnalyzeResponse.CvAnalysis cvAnalysis = new CvAnalyzeResponse.CvAnalysis(
                makeSummary(domains, skills),
                skills,
                domains,
                strengths,
                weaknesses
        );

        List<CvAnalyzeResponse.RecommendedCompetition> recommendations =
                makeRecommendations(recommendationNodes);

        recommendationService.replaceRecommendations(loginUserId, recommendations);
        cvAnalysisStorageService.saveLatest(loginUserId, firstUser.path("name").asText(""), cvAnalysis);

        return new CvAnalyzeResponse(
                loginUserId,
                firstUser.path("name").asText(""),
                cvAnalysis,
                recommendations
        );
    }

    public CvAnalyzeResponse getLatestAnalysis(String loginUserId) {
        CvAnalysisRecord record = cvAnalysisStorageService.getLatest(loginUserId);

        CvAnalyzeResponse.CvAnalysis cvAnalysis = new CvAnalyzeResponse.CvAnalysis(
                record.getSummary(),
                safeList(record.getSkills()),
                safeList(record.getPrimaryDomains()),
                toAnalysisScores(record.getStrengths()),
                toAnalysisScores(record.getWeaknesses())
        );

        return new CvAnalyzeResponse(
                loginUserId,
                record.getExtractedName(),
                cvAnalysis,
                List.of()
        );
    }

    private JsonNode unwrapDlData(JsonNode dlResult) {
        if (dlResult == null || dlResult.isNull() || dlResult.isMissingNode()) {
            throw new IllegalStateException("DL 분석 결과가 비어 있습니다.");
        }

        JsonNode payload = dlResult.has("data") ? dlResult.path("data") : dlResult;

        if (payload == null || payload.isNull() || payload.isMissingNode()) {
            throw new IllegalStateException("DL 분석 결과의 data가 비어 있습니다.");
        }

        return payload;
    }

    private JsonNode resolveFirstUser(JsonNode payload) {
        if (payload.isArray()) {
            if (payload.isEmpty()) {
                throw new IllegalStateException("DL 분석 결과 data 배열이 비어 있습니다.");
            }
            return payload.get(0);
        }

        if (payload.isObject()) {
            JsonNode users = payload.path("users");
            if (users.isArray() && !users.isEmpty()) {
                return users.get(0);
            }

            JsonNode results = payload.path("results");
            if (results.isArray() && !results.isEmpty()) {
                return results.get(0);
            }

            return payload;
        }

        throw new IllegalStateException("지원하지 않는 DL 분석 결과 형식입니다.");
    }

    private JsonNode resolveRecommendationNodes(JsonNode payload, JsonNode firstUser) {
        JsonNode userRecommendations = firstUser.path("recommendations");
        if (userRecommendations.isArray()) {
            return userRecommendations;
        }

        JsonNode payloadRecommendations = payload.path("recommendations");
        if (payloadRecommendations.isArray()) {
            return payloadRecommendations;
        }

        if (payload.isArray()) {
            return payload;
        }

        return userRecommendations;
    }

    private List<CvAnalyzeResponse.RecommendedCompetition> makeRecommendations(JsonNode recommendationNodes) {
        List<CvAnalyzeResponse.RecommendedCompetition> recommendations = new ArrayList<>();

        if (recommendationNodes == null || !recommendationNodes.isArray()) {
            return recommendations;
        }

        recommendationNodes.forEach(node -> {
            Long dlContestId = readLong(node, "contest_id", "contestId", "dlContestId");

            Competition competition = dlContestId == null
                    ? null
                    : competitionRepository.findByDlContestId(dlContestId).orElse(null);

            recommendations.add(new CvAnalyzeResponse.RecommendedCompetition(
                    competition != null ? competition.getId() : null,
                    dlContestId,
                    competition != null ? competition.getName() : readText(node, "title", "name", "contestName"),
                    toPercent(readDouble(node, "final_score", "finalScore", "score")),
                    toPercent(readDouble(node, "domain_score", "domainScore")),
                    toPercent(readDouble(node, "skill_score", "skillScore")),
                    competition != null ? nullToEmpty(competition.getCategory()) : "",
                    competition != null ? nullToEmpty(competition.getApplicationTarget()) : "",
                    competition != null ? nullToEmpty(competition.getOrganizer()) : "",
                    competition != null ? nullToEmpty(competition.getApplicationPeriod()) : "",
                    competition != null ? nullToEmpty(competition.getRepresentativeImageUrl()) : ""
            ));
        });

        return recommendations;
    }

    private List<String> extractFirstRecommendationArray(JsonNode recommendationNodes, String... fieldNames) {
        if (recommendationNodes == null || !recommendationNodes.isArray() || recommendationNodes.isEmpty()) {
            return List.of();
        }

        return extractStringArray(recommendationNodes.get(0), fieldNames);
    }

    private List<String> extractStringArray(JsonNode node, String... fieldNames) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return List.of();
        }

        for (String fieldName : fieldNames) {
            JsonNode target = node.path(fieldName);

            if (target.isArray()) {
                List<String> values = new ArrayList<>();
                target.forEach(value -> {
                    if (!value.asText("").isBlank()) {
                        values.add(value.asText());
                    }
                });

                return values;
            }

            if (target.isTextual() && !target.asText().isBlank()) {
                return List.of(target.asText());
            }
        }

        return List.of();
    }

    private List<CvAnalyzeResponse.AnalysisScore> extractFirstRecommendationScores(
            JsonNode recommendationNodes,
            String... fieldNames
    ) {
        if (recommendationNodes == null || !recommendationNodes.isArray() || recommendationNodes.isEmpty()) {
            return List.of();
        }

        return extractAnalysisScores(recommendationNodes.get(0), fieldNames);
    }

    private List<CvAnalyzeResponse.AnalysisScore> extractAnalysisScores(
            JsonNode node,
            String... fieldNames
    ) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return List.of();
        }

        for (String fieldName : fieldNames) {
            JsonNode target = node.path(fieldName);

            if (target.isArray()) {
                List<CvAnalyzeResponse.AnalysisScore> scores = new ArrayList<>();

                target.forEach(item -> {
                    CvAnalyzeResponse.AnalysisScore score = toAnalysisScore(item);
                    if (score != null) {
                        scores.add(score);
                    }
                });

                if (!scores.isEmpty()) {
                    return scores;
                }
            }

            if (target.isObject()) {
                List<CvAnalyzeResponse.AnalysisScore> scores = new ArrayList<>();

                target.fields().forEachRemaining(entry -> {
                    String name = entry.getKey();
                    JsonNode value = entry.getValue();

                    if (name == null || name.isBlank()) {
                        return;
                    }

                    if (value.isNumber() || value.isTextual()) {
                        scores.add(new CvAnalyzeResponse.AnalysisScore(
                                name,
                                toPercent(readNodeDouble(value)),
                                0,
                                0
                        ));
                        return;
                    }

                    if (value.isObject()) {
                        scores.add(new CvAnalyzeResponse.AnalysisScore(
                                name,
                                toPercent(readDouble(value, "score", "value", "point")),
                                toPercent(readDouble(
                                        value,
                                        "averageScore",
                                        "average_score",
                                        "avgScore",
                                        "avg_score",
                                        "average"
                                )),
                                toDifference(readDouble(value, "difference", "diff", "gap"))
                        ));
                    }
                });

                if (!scores.isEmpty()) {
                    return scores;
                }
            }
        }

        return List.of();
    }

    private CvAnalyzeResponse.AnalysisScore toAnalysisScore(JsonNode item) {
        if (item == null || item.isNull() || item.isMissingNode()) {
            return null;
        }

        if (item.isTextual()) {
            String name = item.asText("");
            if (name.isBlank()) {
                return null;
            }

            return new CvAnalyzeResponse.AnalysisScore(name, 0, 0, 0);
        }

        if (!item.isObject()) {
            return null;
        }

        String name = readText(
                item,
                "name",
                "label",
                "category",
                "title",
                "skill",
                "competency",
                "competencyName",
                "competency_name"
        );

        if (name.isBlank()) {
            return null;
        }

        return new CvAnalyzeResponse.AnalysisScore(
                name,
                toPercent(readDouble(item, "score", "value", "point")),
                toPercent(readDouble(
                        item,
                        "averageScore",
                        "average_score",
                        "avgScore",
                        "avg_score",
                        "average"
                )),
                toDifference(readDouble(item, "difference", "diff", "gap"))
        );
    }

    private String makeSummary(List<String> domains, List<String> skills) {
        String domainText = domains.isEmpty() ? "기술" : String.join(", ", domains);
        String skillText = skills.isEmpty() ? "핵심 역량" : String.join(", ", skills);

        return domainText + " 분야를 중심으로 " + skillText + " 역량을 보유한 지원자입니다.";
    }

    private List<CvAnalyzeResponse.AnalysisScore> toAnalysisScores(List<CvAnalysisScoreItem> items) {
        if (items == null) {
            return List.of();
        }

        return items.stream()
                .map(item -> new CvAnalyzeResponse.AnalysisScore(
                        item.getName(),
                        item.getScore() == null ? 0 : item.getScore(),
                        item.getAverageScore() == null ? 0 : item.getAverageScore(),
                        item.getDifference() == null ? 0 : item.getDifference()
                ))
                .toList();
    }

    private List<String> firstNonEmpty(List<String> first, List<String> second) {
        return first == null || first.isEmpty() ? safeList(second) : first;
    }

    private List<CvAnalyzeResponse.AnalysisScore> firstNonEmptyScores(
            List<CvAnalyzeResponse.AnalysisScore> first,
            List<CvAnalyzeResponse.AnalysisScore> second
    ) {
        return first == null || first.isEmpty() ? safeList(second) : first;
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private Long readLong(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode value = node.path(fieldName);

            if (value.isIntegralNumber()) {
                return value.asLong();
            }

            if (value.isTextual() && !value.asText().isBlank()) {
                try {
                    return Long.parseLong(value.asText());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }

        return null;
    }

    private Double readDouble(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode value = node.path(fieldName);

            if (value.isNumber()) {
                return value.asDouble();
            }

            if (value.isTextual() && !value.asText().isBlank()) {
                try {
                    return Double.parseDouble(value.asText());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }

        return null;
    }

    private Double readNodeDouble(JsonNode value) {
        if (value == null || value.isNull() || value.isMissingNode()) {
            return null;
        }

        if (value.isNumber()) {
            return value.asDouble();
        }

        if (value.isTextual() && !value.asText().isBlank()) {
            try {
                return Double.parseDouble(value.asText());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }

    private String readText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            String value = node.path(fieldName).asText("");

            if (!value.isBlank()) {
                return value;
            }
        }

        return "";
    }

    private int toPercent(Double score) {
        if (score == null) {
            return 0;
        }

        int percent = score <= 1.0
                ? (int) Math.round(score * 100)
                : (int) Math.round(score);

        return Math.max(0, Math.min(100, percent));
    }

    private int toDifference(Double difference) {
        if (difference == null) {
            return 0;
        }

        double normalized = Math.abs(difference) <= 1.0
                ? difference * 100
                : difference;

        return (int) Math.round(normalized);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}