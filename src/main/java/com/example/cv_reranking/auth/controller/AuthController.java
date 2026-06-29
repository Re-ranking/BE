package com.example.cv_reranking.auth.controller;

import com.example.cv_reranking.auth.dto.*;
import com.example.cv_reranking.auth.service.AuthService;
import com.example.cv_reranking.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "인증", description = "회원가입, 로그인, 로그아웃, 이메일 인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 이름, 전공, 한줄 소개, 프로필 이미지를 입력해 회원가입."
    )
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SignupResponse> signup(
            @RequestPart("email") String email,
            @RequestPart("password") String password,
            @RequestPart("name") String name,
            @RequestPart("major") String major,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        SignupRequest request = new SignupRequest(email, password, name, major, description);
        return ApiResponse.success("회원가입 성공", authService.signup(request, profileImage));
    }

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인하고 accessToken을 발급."
    )
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success("로그인 성공", authService.login(request));
    }

    @Operation(
            summary = "로그아웃",
            description = "accessToken을 입력해 로그아웃."
    )
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);
        return ApiResponse.success("로그아웃 성공");
    }

    @Operation(
            summary = "이메일 인증",
            description = "Cognito에서 발송된 인증 코드를 입력해 회원가입을 확정."
    )
    @PostMapping("/confirm")
    public ApiResponse<Void> confirmSignup(@RequestBody ConfirmSignupRequest request) {
        authService.confirmSignup(request);
        return ApiResponse.success("이메일 인증 성공");
    }
}