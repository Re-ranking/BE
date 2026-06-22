package com.example.cv_reranking.analysis.controller;

import com.example.cv_reranking.analysis.dto.CvAnalyzeResponse;
import com.example.cv_reranking.analysis.service.CvAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
public class CvAnalysisController {

    private final CvAnalysisService cvAnalysisService;

    @PostMapping(
            value = "/analyze",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public CvAnalyzeResponse analyzeCv(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        return cvAnalysisService.analyzeCv(file, userId);
    }

    @GetMapping("/latest")
    public CvAnalyzeResponse getLatestCvAnalysis(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        return cvAnalysisService.getLatestAnalysis(userId);
    }
}