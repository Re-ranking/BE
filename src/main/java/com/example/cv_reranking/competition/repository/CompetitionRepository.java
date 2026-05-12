package com.example.cv_reranking.competition.repository;

import com.example.cv_reranking.competition.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
}