package com.example.cv_reranking.analysis.repository;

import com.example.cv_reranking.analysis.entity.CvAnalysisRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CvAnalysisRecordRepository extends JpaRepository<CvAnalysisRecord, Long> {

    Optional<CvAnalysisRecord> findByUserKey(String userKey);
}