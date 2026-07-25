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

        if (recommendations == null || recommendations.isEmpty()) {
            return;
        }

        List<CompetitionRecommendation> entities = recommendations.stream()
                .map(recommendation -> {
                    // 1차: dlContestId로 조회
                    Competition competition = (recommendation.dlContestId() != null)
                            ? competitionRepository.findByDlContestId(recommendation.dlContestId()).orElse(null)
                            : null;

                    // 2차: dlContestId가 DB PK(id)일 가능성 고려
                    if (competition == null && recommendation.dlContestId() != null) {
                        competition = competitionRepository.findById(recommendation.dlContestId()).orElse(null);
                    }

                    // 3차: 공모전 제목(title)으로 조회
                    if (competition == null && recommendation.title() != null && !recommendation.title().isBlank()) {
                        competition = competitionRepository.findByName(recommendation.title()).orElse(null);
                    }

                    // 예외를 던져서 롤백시키는 대신, competition이 null이어도 정상 저장
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