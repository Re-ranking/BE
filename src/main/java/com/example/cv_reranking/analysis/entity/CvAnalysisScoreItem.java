package com.example.cv_reranking.analysis.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Embeddable
public class CvAnalysisScoreItem {

    private String name;

    private int score;

    private int averageScore;

    private int difference;
}