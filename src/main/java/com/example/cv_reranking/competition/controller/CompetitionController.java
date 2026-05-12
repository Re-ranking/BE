package com.example.cv_reranking.competition.controller;

import com.example.cv_reranking.competition.dto.CompetitionDetailResponse;
import com.example.cv_reranking.competition.dto.CompetitionListResponse;
import com.example.cv_reranking.competition.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    // 전체 목록 / 분야별 / 대상별 조회
    // filter가 없으면 전체 목록을 기본 마감임박순으로 조회
    @GetMapping
    public List<CompetitionListResponse> getCompetitionList(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String value
    ) {
        return competitionService.getCompetitionList(filter, value);
    }

    // 공모전 이름 검색
    @GetMapping("/search")
    public List<CompetitionListResponse> searchCompetitions(
            @RequestParam String keyword
    ) {
        return competitionService.searchCompetitionsByName(keyword);
    }

    // 공모전 상세 조회
    @GetMapping("/{competitionId}")
    public CompetitionDetailResponse getCompetitionDetail(@PathVariable Long competitionId) {
        return competitionService.getCompetitionDetail(competitionId);
    }
}