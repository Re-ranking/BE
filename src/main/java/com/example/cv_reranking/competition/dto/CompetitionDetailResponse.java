package com.example.cv_reranking.competition.dto;

import com.example.cv_reranking.competition.entity.Competition;

public record CompetitionDetailResponse(
        Long competitionId,
        Long dlContestId,
        String name,
        String sourceUrl,
        String category,
        String domains,
        String skills,
        String applicationTarget,
        String organizer,
        String applicationPeriod,
        String totalPrize,
        String firstPrize,
        String homepage,
        String representativeImageUrl,
        String description
) {
    public static CompetitionDetailResponse from(Competition competition) {
        return new CompetitionDetailResponse(
                competition.getId(),
                competition.getDlContestId(),
                competition.getName(),
                competition.getSourceUrl(),
                competition.getCategory(),
                competition.getDomains(),
                competition.getSkills(),
                competition.getApplicationTarget(),
                competition.getOrganizer(),
                competition.getApplicationPeriod(),
                competition.getTotalPrize(),
                competition.getFirstPrize(),
                competition.getHomepage(),
                competition.getRepresentativeImageUrl(),
                competition.getDescription()
        );
    }
}