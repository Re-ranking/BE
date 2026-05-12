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

    // 전체 목록 / 분야별 / 대상별 조회
    public List<CompetitionListResponse> getCompetitionList(String filter, String value) {
        // filter가 없으면 전체 목록을 기본 마감임박순으로 조회
        if (filter == null || filter.isBlank()) {
            return getDefaultList();
        }

        String selectedFilter = filter.toLowerCase(Locale.ROOT);

        return switch (selectedFilter) {
            case "category" -> getCategoryList(value);
            case "target" -> getTargetList(value);
            default -> throw new IllegalArgumentException("지원하지 않는 필터입니다: " + filter);
        };
    }

    // 공모전 상세 조회
    public CompetitionDetailResponse getCompetitionDetail(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공모전입니다. id=" + competitionId));

        return CompetitionDetailResponse.from(competition);
    }

    // 기본 전체 목록: 마감임박순
    private List<CompetitionListResponse> getDefaultList() {
        return competitionRepository.findAll().stream()
                .sorted(deadlineComparator())
                .map(CompetitionListResponse::from)
                .toList();
    }

    // 분야별 조회
    private List<CompetitionListResponse> getCategoryList(String category) {
        validateValue(category, "분야");

        return competitionRepository.findAll().stream()
                .filter(competition -> containsText(competition.getCategory(), category))
                .map(CompetitionListResponse::from)
                .toList();
    }

    // 대상별 조회
    private List<CompetitionListResponse> getTargetList(String target) {
        validateValue(target, "응모대상");

        return competitionRepository.findAll().stream()
                .filter(competition -> containsText(competition.getApplicationTarget(), target))
                .map(CompetitionListResponse::from)
                .toList();
    }

    // 공모전 이름 검색
    public List<CompetitionListResponse> searchCompetitionsByName(String keyword) {
        validateValue(keyword, "검색어");

        return competitionRepository.findAll().stream()
                .filter(competition -> containsText(competition.getName(), keyword))
                .map(CompetitionListResponse::from)
                .toList();
    }

    // value 필수값 검증
    private void validateValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " 값이 필요합니다.");
        }
    }

    // 문자열 포함 여부 확인
    // 영어는 대소문자 구분 없이 검색되고, 한글도 정상 검색됨
    private boolean containsText(String source, String keyword) {
        if (source == null || keyword == null || keyword.isBlank()) {
            return false;
        }

        String normalizedSource = source.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);

        return normalizedSource.contains(normalizedKeyword);
    }

    // 기본 목록에서만 사용하는 마감임박순 정렬 기준
    private Comparator<Competition> deadlineComparator() {
        return Comparator
                .comparing(this::deadlineSortDate)
                .thenComparing(
                        Competition::getId,
                        Comparator.nullsLast(Comparator.reverseOrder())
                );
    }

    // 마감일이 가까운 공모전이 먼저 오도록 처리
    private LocalDate deadlineSortDate(Competition competition) {
        LocalDate endDate = competition.getApplicationEndDate();
        LocalDate today = LocalDate.now();

        // 마감일이 없으면 맨 뒤로 보냄
        if (endDate == null) {
            return LocalDate.MAX;
        }

        // 이미 마감된 공모전도 맨 뒤로 보냄
        if (endDate.isBefore(today)) {
            return LocalDate.MAX;
        }

        return endDate;
    }
}