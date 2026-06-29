package com.example.cv_reranking.recommendation.controller;

import com.example.cv_reranking.recommendation.dto.TeamMemberRecommendationResponse;
import com.example.cv_reranking.recommendation.service.TeamMemberRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/mypage/recommendations")
@RequiredArgsConstructor
@Tag(name = "마이페이지 추천", description = "마이페이지에서 확인하는 공모전 팀원 추천 API")
public class TeamMemberRecommendationController {

    private final TeamMemberRecommendationService teamMemberRecommendationService;

    @Operation(
            summary = "내 팀원 추천 목록 조회",
            description = """
                    로그인한 사용자에게 추천된 팀원 목록을 조회.

                    테스트 방법:
                    1. 먼저 성향 설문 제출 API를 실행해 사용자 성향 데이터를 저장.
                    2. Swagger 우측 상단 Authorize에 accessToken을 입력.
                    3. Execute를 누름.
                    4. 추천 팀원 목록이 반환되면 성공.

                    반환 정보:
                    - 추천 팀원 정보
                    - 추천 점수
                    - 추천 사유
                    - 성향/협업 스타일 기반 매칭 결과
                    """
    )
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