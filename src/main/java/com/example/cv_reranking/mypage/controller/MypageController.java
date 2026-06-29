package com.example.cv_reranking.mypage.controller;

import com.example.cv_reranking.mypage.dto.MypageCvUpdateRequest;
import com.example.cv_reranking.mypage.dto.MypageProfileUpdateRequest;
import com.example.cv_reranking.mypage.dto.MypageResponse;
import com.example.cv_reranking.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.example.cv_reranking.mypage.dto.MypageCvResponse;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "마이페이지", description = "내 프로필, CV 정보 조회 및 수정 API")
public class MypageController {

    private final MypageService mypageService;

    @Operation(
            summary = "내 CV 정보 조회",
            description = """
                    로그인한 사용자의 마이페이지 CV 정보를 조회.

                    테스트 방법:
                    1. 로그인 후 발급받은 accessToken을 Swagger 우측 상단 Authorize에 입력.
                    2. Execute를 누름.
                    3. 저장된 CV 분석 결과와 사용자가 수정한 기술/관심사/프로젝트/수상 정보가 반환.
                    """
    )
    @GetMapping("/cv")
    public MypageCvResponse getMyCvInfo(@AuthenticationPrincipal Jwt jwt) {
        return mypageService.getMyCvInfo(jwt);
    }

    @Operation(
            summary = "내 프로필 수정",
            description = """
                    로그인한 사용자의 기본 프로필 정보를 수정.

                    테스트 방법:
                    1. Authorize에 accessToken을 입력.
                    2. name, major, introduction 중 수정할 값만 입력.
                    3. profileImage에는 jpg, png, webp 등 이미지 파일을 업로드.
                    4. Execute 후 응답의 profileImage 값이 S3 URL로 내려오면 이미지 업로드가 성공.

                    참고:
                    - 모든 항목은 선택값.
                    - profileImage를 넣지 않으면 기존 이미지가 유지.
                    """
    )
    @PatchMapping(
            value = "/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public MypageResponse updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart(required = false) String name,
            @RequestPart(required = false) String major,
            @RequestPart(required = false) String introduction,
            @RequestPart(required = false) MultipartFile profileImage
    ) {
        return mypageService.updateProfile(jwt, name, major, introduction, profileImage);
    }

    @Operation(
            summary = "내 CV 정보 직접 수정",
            description = """
                    마이페이지에서 사용자가 CV 기반 정보를 직접 수정.

                    테스트 방법:
                    1. Authorize에 accessToken을 입력.
                    2. Request body에 skills, interests, projects, awards 값을 입력.
                    3. Execute를 누름.
                    4. 응답에 수정된 마이페이지 정보가 반환되면 성공.
                    """
    )
    @PatchMapping("/cv")
    public MypageResponse updateCv(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody MypageCvUpdateRequest request
    ) {
        return mypageService.updateCv(jwt, request);
    }
}