package com.example.cv_reranking.competition.service;

import com.example.cv_reranking.competition.dto.CompetitionDetailResponse;
import com.example.cv_reranking.competition.dto.CompetitionListResponse;
import com.example.cv_reranking.competition.entity.Competition;
import com.example.cv_reranking.competition.repository.CompetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    public List<CompetitionListResponse> getCompetitionList(String filter, String value) {
        if (filter == null || filter.isBlank()) {
            return getDefaultList();
        }

        String selectedFilter = filter.toLowerCase(Locale.ROOT);

        return switch (selectedFilter) {
            case "category", "domain" -> getCategoryList(value);
            case "skill" -> getSkillList(value);
            case "target" -> getTargetList(value);
            default -> throw new IllegalArgumentException("지원하지 않는 필터입니다: " + filter);
        };
    }

    public CompetitionDetailResponse getCompetitionDetail(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공모전입니다. id=" + competitionId));

        return CompetitionDetailResponse.from(competition);
    }

    public CompetitionDetailResponse getCompetitionDetailByDlContestId(Long dlContestId) {
        Competition competition = competitionRepository.findByDlContestId(dlContestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 DL 공모전입니다. dlContestId=" + dlContestId));

        return CompetitionDetailResponse.from(competition);
    }

    public List<CompetitionListResponse> searchCompetitionsByName(String keyword) {
        validateValue(keyword, "검색어");

        return competitionRepository.findAll().stream()
                .filter(competition -> containsText(competition.getName(), keyword))
                .map(CompetitionListResponse::from)
                .toList();
    }

    private List<CompetitionListResponse> getDefaultList() {
        return competitionRepository.findAll().stream()
                .sorted(deadlineComparator())
                .map(CompetitionListResponse::from)
                .toList();
    }

    private List<CompetitionListResponse> getCategoryList(String category) {
        validateValue(category, "분야");

        return competitionRepository.findAll().stream()
                .filter(competition ->
                        containsText(competition.getCategory(), category)
                                || containsText(competition.getDomains(), category)
                )
                .map(CompetitionListResponse::from)
                .toList();
    }

    private List<CompetitionListResponse> getSkillList(String skill) {
        validateValue(skill, "기술");

        return competitionRepository.findAll().stream()
                .filter(competition -> containsText(competition.getSkills(), skill))
                .map(CompetitionListResponse::from)
                .toList();
    }

    private List<CompetitionListResponse> getTargetList(String target) {
        validateValue(target, "응모대상");

        return competitionRepository.findAll().stream()
                .filter(competition -> containsText(competition.getApplicationTarget(), target))
                .map(CompetitionListResponse::from)
                .toList();
    }

    private void validateValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " 값이 필요합니다.");
        }
    }

    private boolean containsText(String source, String keyword) {
        if (source == null || keyword == null || keyword.isBlank()) {
            return false;
        }

        return source.toLowerCase(Locale.ROOT)
                .contains(keyword.trim().toLowerCase(Locale.ROOT));
    }

    private Comparator<Competition> deadlineComparator() {
        return Comparator
                .comparing(this::deadlineSortDate)
                .thenComparing(Competition::getId, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private LocalDate deadlineSortDate(Competition competition) {
        LocalDate endDate = competition.getApplicationEndDate();
        LocalDate today = LocalDate.now();

        if (endDate == null || endDate.isBefore(today)) {
            return LocalDate.MAX;
        }

        return endDate;
    }
}