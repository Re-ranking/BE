package com.example.cv_reranking.analysis.controller;

import com.example.cv_reranking.analysis.dto.CvAnalyzeResponse;
import com.example.cv_reranking.analysis.service.CvAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CV 분석", description = "CV 파일 업로드, 분석 결과 조회, 공모전 추천 API")
@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
public class CvAnalysisController {

    private final CvAnalysisService cvAnalysisService;

    @Operation(
            summary = "CV 분석 및 공모전 추천",
            description = """
                CV 파일을 업로드하면 DL 서버로 분석 요청을 보내고,
                분석된 기술/도메인 정보를 기반으로 공모전을 추천.

                테스트 방법:
                1. Authorize에 로그인 accessToken 입력
                2. file 항목에 PDF, PNG, JPG 중 하나 업로드
                3. Execute 실행
                """
    )
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