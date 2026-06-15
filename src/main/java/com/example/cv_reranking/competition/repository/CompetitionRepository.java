package com.example.cv_reranking.competition.repository;

import com.example.cv_reranking.competition.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    Optional<Competition> findByDlContestId(Long dlContestId);
}