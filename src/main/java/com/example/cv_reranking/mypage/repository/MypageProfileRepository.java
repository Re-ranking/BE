package com.example.cv_reranking.mypage.repository;

import com.example.cv_reranking.mypage.entity.MypageProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MypageProfileRepository extends JpaRepository<MypageProfile, Long> {

    Optional<MypageProfile> findByUserKey(String userKey);
}