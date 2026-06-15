package com.example.cv_reranking.recommendation.entity;

import com.example.cv_reranking.competition.entity.Competition;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "competition_recommendations")
public class CompetitionRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    private Long dlContestId;

    @Column(length = 500)
    private String title;

    private int score;
    private int domainScore;
    private int skillScore;

    private LocalDateTime createdAt;
}