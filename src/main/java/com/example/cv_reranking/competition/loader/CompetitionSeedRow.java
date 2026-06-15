package com.example.cv_reranking.competition.loader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompetitionSeedRow {

    @JsonProperty("contest_id")
    private Long contestId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("domains")
    private List<String> domains = List.of();

    @JsonProperty("skills")
    private List<String> skills = List.of();

    @JsonProperty("source_url")
    private String sourceUrl;

    @JsonProperty("target")
    private String target;

    @JsonProperty("host")
    private String host;

    @JsonProperty("period")
    private String period;

    @JsonProperty("total_prize")
    private String totalPrize;

    @JsonProperty("first_prize")
    private String firstPrize;

    @JsonProperty("homepage")
    private String homepage;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("description")
    private String description;
}