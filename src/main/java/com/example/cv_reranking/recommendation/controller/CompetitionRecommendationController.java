package com.example.cv_reranking.recommendation.controller;

import com.example.cv_reranking.recommendation.dto.CompetitionRecommendationResponse;
import com.example.cv_reranking.recommendation.service.CompetitionRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage/recommendations")
@RequiredArgsConstructor
public class CompetitionRecommendationController {

    private final CompetitionRecommendationService recommendationService;

    @GetMapping("/competitions")
    public List<CompetitionRecommendationResponse> getMyCompetitionRecommendations(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return recommendationService.getMyCompetitionRecommendations(jwt.getSubject());
    }
}