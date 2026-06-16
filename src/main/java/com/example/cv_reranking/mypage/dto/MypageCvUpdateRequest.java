package com.example.cv_reranking.mypage.dto;

import com.example.cv_reranking.mypage.entity.ProjectItem;

import java.util.List;

public record MypageCvUpdateRequest(
        List<String> skills,
        List<String> interests,
        List<ProjectItem> projects,
        List<String> awards
) {
}