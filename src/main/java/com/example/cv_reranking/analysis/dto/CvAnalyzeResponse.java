package com.example.cv_reranking.analysis.dto;

import java.util.List;

public record CvAnalyzeResponse(
        String userId,
        String name,
        CvAnalysis cvAnalysis,
        List<RecommendedCompetition> recommendations
) {
    public record CvAnalysis(
            String summary,
            List<String> skills,
            List<String> primaryDomains,
            List<AnalysisScore> strengths,
            List<AnalysisScore> weaknesses
    ) {
    }

    public record AnalysisScore(
            String name,
            int score
    ) {
    }

    public record RecommendedCompetition(
            Long competitionId,
            Long dlContestId,
            String title,
            int score,
            int domainScore,
            int skillScore,
            String category,
            String applicationTarget,
            String organizer,
            String applicationPeriod,
            String representativeImageUrl
    ) {
    }
}