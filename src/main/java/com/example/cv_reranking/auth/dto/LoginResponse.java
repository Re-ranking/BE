package com.example.cv_reranking.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private Long memberId;
    private String email;
    private String name;
    private String major;
    private String profileImage;
    private String description;
}