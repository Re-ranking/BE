package com.example.cv_reranking.analysis.service;

import com.example.cv_reranking.analysis.dto.CvAnalyzeResponse;
import com.example.cv_reranking.analysis.entity.CvAnalysisRecord;
import com.example.cv_reranking.analysis.entity.CvAnalysisScoreItem;
import com.example.cv_reranking.analysis.repository.CvAnalysisRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CvAnalysisStorageService {

    private final CvAnalysisRecordRepository repository;

    @Transactional
    public void saveLatest(String userKey, String extractedName, CvAnalyzeResponse.CvAnalysis cvAnalysis) {
        List<CvAnalysisScoreItem> strengths = cvAnalysis.strengths().stream()
                .map(score -> new CvAnalysisScoreItem(
                        score.name(),
                        score.score(),
                        score.averageScore(),
                        score.difference()
                ))
                .toList();

        List<CvAnalysisScoreItem> weaknesses = cvAnalysis.weaknesses().stream()
                .map(score -> new CvAnalysisScoreItem(
                        score.name(),
                        score.score(),
                        score.averageScore(),
                        score.difference()
                ))
                .toList();

        CvAnalysisRecord record = repository.findByUserKey(userKey)
                .orElseGet(() -> CvAnalysisRecord.builder()
                        .userKey(userKey)
                        .build());

        record.update(
                extractedName,
                cvAnalysis.summary(),
                cvAnalysis.skills(),
                cvAnalysis.primaryDomains(),
                strengths,
                weaknesses
        );

        repository.save(record);
    }

    @Transactional(readOnly = true)
    public CvAnalysisRecord getLatest(String userKey) {
        return repository.findByUserKey(userKey)
                .orElseThrow(() -> new IllegalArgumentException("저장된 CV 분석 결과가 없습니다."));
    }

    @Transactional(readOnly = true)
    public CvAnalysisRecord getLatestOrNull(String userKey) {
        return repository.findByUserKey(userKey).orElse(null);
    }
}