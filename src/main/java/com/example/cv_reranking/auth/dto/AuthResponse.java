package com.example.cv_reranking.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private Long memberId;
    private String email;
    private String name;
    private String major;
    private String profileImage;
}