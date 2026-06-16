package com.example.cv_reranking.personality.repository;

import com.example.cv_reranking.auth.entity.Member;
import com.example.cv_reranking.personality.entity.PersonalitySurvey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalitySurveyRepository extends JpaRepository<PersonalitySurvey, Long> {
    Optional<PersonalitySurvey> findByMember(Member member);
}