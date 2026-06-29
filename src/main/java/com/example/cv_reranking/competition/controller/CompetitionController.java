package com.example.cv_reranking.competition.controller;

import com.example.cv_reranking.competition.dto.CompetitionDetailResponse;
import com.example.cv_reranking.competition.dto.CompetitionListResponse;
import com.example.cv_reranking.competition.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "공모전", description = "공모전 목록, 검색, 상세 조회 API")
@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    @Operation(
            summary = "공모전 목록 조회",
            description = """
                전체 공모전 목록을 조회.

                filter와 value를 넣으면 특정 조건으로 필터링.
                예: filter=category, value=AI
                """
    )
    @GetMapping
    public List<CompetitionListResponse> getCompetitionList(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String value
    ) {
        return competitionService.getCompetitionList(filter, value);
    }

    @Operation(
            summary = "공모전 검색",
            description = "keyword를 기준으로 공모전 이름을 검색."
    )
    @GetMapping("/search")
    public List<CompetitionListResponse> searchCompetitions(@RequestParam String keyword) {
        return competitionService.searchCompetitionsByName(keyword);
    }

    @Operation(
            summary = "공모전 상세 조회",
            description = "competitionId로 공모전 상세 정보를 조회."
    )
    @GetMapping("/{competitionId}")
    public CompetitionDetailResponse getCompetitionDetail(@PathVariable Long competitionId) {
        return competitionService.getCompetitionDetail(competitionId);
    }

    @Operation(
            summary = "이건 참고용으로 만들어서 쓰는 데 없어!!",
            description = "참고용이라 안 쓰이는 거에우."
    )
    @GetMapping("/dl/{dlContestId}")
    public CompetitionDetailResponse getCompetitionDetailByDlContestId(@PathVariable Long dlContestId) {
        return competitionService.getCompetitionDetailByDlContestId(dlContestId);
    }
}