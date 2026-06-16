package com.example.cv_reranking.analysis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "cv_analysis_records")
public class CvAnalysisRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userKey;

    private String extractedName;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cv_analysis_skills", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cv_analysis_domains", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "domain")
    @Builder.Default
    private List<String> primaryDomains = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cv_analysis_strengths", joinColumns = @JoinColumn(name = "analysis_id"))
    @Builder.Default
    private List<CvAnalysisScoreItem> strengths = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cv_analysis_weaknesses", joinColumns = @JoinColumn(name = "analysis_id"))
    @Builder.Default
    private List<CvAnalysisScoreItem> weaknesses = new ArrayList<>();

    public void update(String extractedName, String summary, List<String> skills,
                       List<String> primaryDomains, List<CvAnalysisScoreItem> strengths,
                       List<CvAnalysisScoreItem> weaknesses) {
        this.extractedName = extractedName;
        this.summary = summary;
        this.skills = new ArrayList<>(skills);
        this.primaryDomains = new ArrayList<>(primaryDomains);
        this.strengths = new ArrayList<>(strengths);
        this.weaknesses = new ArrayList<>(weaknesses);
    }
}