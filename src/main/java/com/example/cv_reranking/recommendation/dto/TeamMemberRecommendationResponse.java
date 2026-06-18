package com.example.cv_reranking.recommendation.dto;

import java.util.List;

public record TeamMemberRecommendationResponse(
        int rank,
        String candidateUserId,
        String name,
        String role,
        int score,
        List<String> skills,
        List<String> domains,
        String reason
) {
}