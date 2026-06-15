package com.example.cv_reranking.recommendation.dto;

import com.example.cv_reranking.competition.entity.Competition;
import com.example.cv_reranking.recommendation.entity.CompetitionRecommendation;

public record CompetitionRecommendationResponse(
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
    public static CompetitionRecommendationResponse from(CompetitionRecommendation recommendation) {
        Competition competition = recommendation.getCompetition();

        return new CompetitionRecommendationResponse(
                competition != null ? competition.getId() : null,
                recommendation.getDlContestId(),
                competition != null ? competition.getName() : recommendation.getTitle(),
                recommendation.getScore(),
                recommendation.getDomainScore(),
                recommendation.getSkillScore(),
                competition != null ? competition.getCategory() : "",
                competition != null ? competition.getApplicationTarget() : "",
                competition != null ? competition.getOrganizer() : "",
                competition != null ? competition.getApplicationPeriod() : "",
                competition != null ? competition.getRepresentativeImageUrl() : ""
        );
    }
}