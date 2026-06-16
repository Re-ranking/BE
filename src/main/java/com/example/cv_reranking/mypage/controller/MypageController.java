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

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/cv")
    public MypageCvResponse getMyCvInfo(@AuthenticationPrincipal Jwt jwt) {
        return mypageService.getMyCvInfo(jwt);
    }

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

    @PatchMapping("/cv")
    public MypageResponse updateCv(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody MypageCvUpdateRequest request
    ) {
        return mypageService.updateCv(jwt, request);
    }
}