package com.example.cv_reranking.personality.controller;

import com.example.cv_reranking.global.response.ApiResponse;
import com.example.cv_reranking.personality.dto.PersonalitySurveyResponse;
import com.example.cv_reranking.personality.dto.PersonalitySurveySaveRequest;
import com.example.cv_reranking.personality.service.PersonalitySurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personality-surveys")
@RequiredArgsConstructor
public class PersonalitySurveyController {

    private final PersonalitySurveyService personalitySurveyService;

    @PostMapping("/draft")
    public ApiResponse<PersonalitySurveyResponse> saveDraft(
            Authentication authentication,
            @RequestBody PersonalitySurveySaveRequest request
    ) {
        return ApiResponse.success(
                "성향 테스트 임시 저장 성공",
                personalitySurveyService.saveDraft(resolveCognitoSub(authentication), request)
        );
    }

    @PostMapping("/submit")
    public ApiResponse<PersonalitySurveyResponse> submit(
            Authentication authentication,
            @RequestBody PersonalitySurveySaveRequest request
    ) {
        return ApiResponse.success(
                "성향 테스트 최종 제출 성공",
                personalitySurveyService.submit(resolveCognitoSub(authentication), request)
        );
    }

    @GetMapping("/me")
    public ApiResponse<PersonalitySurveyResponse> getMySurvey(Authentication authentication) {
        return ApiResponse.success(
                "내 성향 테스트 조회 성공",
                personalitySurveyService.getMySurvey(resolveCognitoSub(authentication))
        );
    }

    private String resolveCognitoSub(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }

        return authentication.getName();
    }
}