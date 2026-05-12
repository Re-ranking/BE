package com.example.cv_reranking.competition.loader;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompetitionSeedRow {

    @JsonProperty("name")
    private String name;

    @JsonProperty("source_url")
    private String sourceUrl;

    @JsonProperty("분야")
    private String category;

    @JsonProperty("응모대상")
    private String applicationTarget;

    @JsonProperty("주최/주관")
    private String organizer;

    @JsonProperty("접수기간")
    private String applicationPeriod;

    @JsonProperty("총 상금")
    private String totalPrize;

    @JsonProperty("1등 상금")
    private String firstPrize;

    @JsonProperty("홈페이지")
    private String homepage;

    @JsonAlias({
            "공모전 대표 사진",
            "대표사진",
            "대표 이미지",
            "image_url",
            "poster_url",
            "thumbnail"
    })
    private String representativeImageUrl;
}