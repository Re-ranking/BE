package com.example.cv_reranking.competition.service;

import com.example.cv_reranking.competition.dto.CompetitionDetailResponse;
import com.example.cv_reranking.competition.dto.CompetitionListResponse;
import com.example.cv_reranking.competition.entity.Competition;
import com.example.cv_reranking.competition.repository.CompetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    public List<CompetitionListResponse> getCompetitionList() {
        return competitionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(CompetitionListResponse::from)
                .toList();
    }

    public CompetitionDetailResponse getCompetitionDetail(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공모전입니다. id=" + competitionId));

        return CompetitionDetailResponse.from(competition);
    }
}