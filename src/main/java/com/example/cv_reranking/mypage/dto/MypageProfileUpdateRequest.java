package com.example.cv_reranking.mypage.dto;

public record MypageProfileUpdateRequest(
        String name,
        String major,
        String introduction
) {
}