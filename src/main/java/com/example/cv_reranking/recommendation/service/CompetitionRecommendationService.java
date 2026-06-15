package com.example.cv_reranking.recommendation.service;

import com.example.cv_reranking.analysis.dto.CvAnalyzeResponse;
import com.example.cv_reranking.competition.entity.Competition;
import com.example.cv_reranking.competition.repository.CompetitionRepository;
import com.example.cv_reranking.recommendation.dto.CompetitionRecommendationResponse;
import com.example.cv_reranking.recommendation.entity.CompetitionRecommendation;
import com.example.cv_reranking.recommendation.repository.CompetitionRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionRecommendationService {

    private final CompetitionRecommendationRepository recommendationRepository;
    private final CompetitionRepository competitionRepository;

    @Transactional
    public void replaceRecommendations(String userId, List<CvAnalyzeResponse.RecommendedCompetition> recommendations) {
        recommendationRepository.deleteByUserId(userId);

        List<CompetitionRecommendation> entities = recommendations.stream()
                .map(recommendation -> {
                    Competition competition = competitionRepository.findByDlContestId(recommendation.dlContestId())
                            .orElse(null);

                    return CompetitionRecommendation.builder()
                            .userId(userId)
                            .competition(competition)
                            .dlContestId(recommendation.dlContestId())
                            .title(recommendation.title())
                            .score(recommendation.score())
                            .domainScore(recommendation.domainScore())
                            .skillScore(recommendation.skillScore())
                            .createdAt(LocalDateTime.now())
                            .build();
                })
                .toList();

        recommendationRepository.saveAll(entities);
    }

    @Transactional(readOnly = true)
    public List<CompetitionRecommendationResponse> getMyCompetitionRecommendations(String userId) {
        return recommendationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(CompetitionRecommendationResponse::from)
                .toList();
    }
}