package com.example.cv_reranking.competition.dto;

import com.example.cv_reranking.competition.entity.Competition;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CompetitionDetailResponse(
        Long competitionId,

        String name,

        @JsonProperty("source_url")
        String sourceUrl,

        @JsonProperty("분야")
        String category,

        @JsonProperty("응모대상")
        String applicationTarget,

        @JsonProperty("주최/주관")
        String organizer,

        @JsonProperty("접수기간")
        String applicationPeriod,

        @JsonProperty("총상금")
        String totalPrize,

        @JsonProperty("1등 상금")
        String firstPrize,

        @JsonProperty("홈페이지")
        String homepage,

        @JsonProperty("공모전 대표 사진")
        String representativeImageUrl
) {
    public static CompetitionDetailResponse from(Competition competition) {
        return new CompetitionDetailResponse(
                competition.getId(),
                competition.getName(),
                competition.getSourceUrl(),
                competition.getCategory(),
                competition.getApplicationTarget(),
                competition.getOrganizer(),
                competition.getApplicationPeriod(),
                competition.getTotalPrize(),
                competition.getFirstPrize(),
                competition.getHomepage(),
                competition.getRepresentativeImageUrl()
        );
    }
}