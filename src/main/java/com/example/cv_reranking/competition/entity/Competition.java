package com.example.cv_reranking.competition.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "competitions")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dl_contest_id", unique = true)
    private Long dlContestId;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(columnDefinition = "TEXT")
    private String category;

    @Column(columnDefinition = "TEXT")
    private String domains;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(name = "application_target", columnDefinition = "TEXT")
    private String applicationTarget;

    private String organizer;

    @Column(name = "application_period")
    private String applicationPeriod;

    @Column(name = "application_end_date")
    private LocalDate applicationEndDate;

    @Column(name = "total_prize")
    private String totalPrize;

    @Column(name = "first_prize")
    private String firstPrize;

    @Column(columnDefinition = "TEXT")
    private String homepage;

    @Column(name = "representative_image_url", columnDefinition = "TEXT")
    private String representativeImageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;
}