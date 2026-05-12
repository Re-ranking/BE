package com.example.cv_reranking.competition.dto;

import com.example.cv_reranking.competition.entity.Competition;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CompetitionListResponse(
        Long competitionId,

        String name,

        @JsonProperty("분야")
        String category,

        @JsonProperty("응모대상")
        String applicationTarget,

        @JsonProperty("주최/주관")
        String organizer,

        @JsonProperty("접수기간")
        String applicationPeriod,

        @JsonProperty("공모전 대표 사진")
        String representativeImageUrl
) {
    public static CompetitionListResponse from(Competition competition) {
        return new CompetitionListResponse(
                competition.getId(),
                competition.getName(),
                competition.getCategory(),
                competition.getApplicationTarget(),
                competition.getOrganizer(),
                competition.getApplicationPeriod(),
                competition.getRepresentativeImageUrl()
        );
    }
}
