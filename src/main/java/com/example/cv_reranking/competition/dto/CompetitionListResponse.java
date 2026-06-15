package com.example.cv_reranking.competition.dto;

import com.example.cv_reranking.competition.entity.Competition;

public record CompetitionListResponse(
        Long competitionId,
        Long dlContestId,
        String name,
        String category,
        String applicationTarget,
        String organizer,
        String applicationPeriod,
        String representativeImageUrl
) {
    public static CompetitionListResponse from(Competition competition) {
        return new CompetitionListResponse(
                competition.getId(),
                competition.getDlContestId(),
                competition.getName(),
                competition.getCategory(),
                competition.getApplicationTarget(),
                competition.getOrganizer(),
                competition.getApplicationPeriod(),
                competition.getRepresentativeImageUrl()
        );
    }
}