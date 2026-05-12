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

    // name
    @Column(nullable = false, length = 500)
    private String name;

    // source_url
    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    // 분야
    private String category;

    // 응모대상
    @Column(name = "application_target")
    private String applicationTarget;

    // 주최/주관
    private String organizer;

    // 접수기간
    @Column(name = "application_period")
    private String applicationPeriod;

    // 접수기간에서 마지막 날짜만 추출한 값
    @Column(name = "application_end_date")
    private LocalDate applicationEndDate;

    // 총상금
    @Column(name = "total_prize")
    private String totalPrize;

    // 1등 상금
    @Column(name = "first_prize")
    private String firstPrize;

    // 홈페이지
    @Column(columnDefinition = "TEXT")
    private String homepage;

    // 공모전 대표 사진
    @Column(name = "representative_image_url", columnDefinition = "TEXT")
    private String representativeImageUrl;
}