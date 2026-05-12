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

    @GetMapping
    public List<CompetitionListResponse> getCompetitionList() {
        return competitionService.getCompetitionList();
    }

    @GetMapping("/{competitionId}")
    public CompetitionDetailResponse getCompetitionDetail(@PathVariable Long competitionId) {
        return competitionService.getCompetitionDetail(competitionId);
    }
}