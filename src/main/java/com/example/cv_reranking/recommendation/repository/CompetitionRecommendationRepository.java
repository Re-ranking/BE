package com.example.cv_reranking.recommendation.repository;

import com.example.cv_reranking.recommendation.entity.CompetitionRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitionRecommendationRepository extends JpaRepository<CompetitionRecommendation, Long> {

    List<CompetitionRecommendation> findByUserIdOrderByCreatedAtDesc(String userId);

    void deleteByUserId(String userId);
}