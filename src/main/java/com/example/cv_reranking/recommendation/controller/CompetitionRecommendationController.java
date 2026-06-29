package com.example.cv_reranking.recommendation.controller;

import com.example.cv_reranking.recommendation.dto.CompetitionRecommendationResponse;
import com.example.cv_reranking.recommendation.service.CompetitionRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/mypage/recommendations")
@RequiredArgsConstructor
@Tag(name = "마이페이지 추천", description = "마이페이지에서 확인하는 공모전/팀원 추천 API")
public class CompetitionRecommendationController {

    private final CompetitionRecommendationService recommendationService;

    @Operation(
            summary = "내 공모전 추천 목록 조회",
            description = """
                    로그인한 사용자에게 추천된 공모전 목록을 조회.

                    테스트 방법:
                    1. 먼저 /api/cv/analyze에서 CV 분석을 실행해 공모전 추천 결과를 생성.
                    2. Swagger 우측 상단 Authorize에 accessToken을 입력.
                    3. Execute를 누름.
                    4. 추천 공모전 목록이 반환되면 성공.

                    반환 정보:
                    - 추천 공모전 ID
                    - 공모전명
                    - 추천 점수
                    - 분야/기술 점수
                    - 공모전 기본 정보
                    """
    )
    @GetMapping("/competitions")
    public List<CompetitionRecommendationResponse> getMyCompetitionRecommendations(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return recommendationService.getMyCompetitionRecommendations(jwt.getSubject());
    }
}