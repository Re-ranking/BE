package com.example.cv_reranking.recommendation.controller;

import com.example.cv_reranking.recommendation.dto.TeamMemberRecommendationResponse;
import com.example.cv_reranking.recommendation.service.TeamMemberRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage/recommendations")
@RequiredArgsConstructor
public class TeamMemberRecommendationController {

    private final TeamMemberRecommendationService teamMemberRecommendationService;

    @GetMapping("/team-members")
    public List<TeamMemberRecommendationResponse> recommendTeamMembers(Authentication authentication) {
        return teamMemberRecommendationService.recommendTeamMembers(resolveCognitoSub(authentication));
    }

    private String resolveCognitoSub(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }

        return authentication.getName();
    }
}