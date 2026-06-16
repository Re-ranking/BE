package com.example.cv_reranking.auth.controller;

import com.example.cv_reranking.auth.dto.*;
import com.example.cv_reranking.auth.service.AuthService;
import com.example.cv_reranking.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success("로그인 성공", authService.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);
        return ApiResponse.success("로그아웃 성공");
    }

    @PostMapping("/confirm")
    public ApiResponse<Void> confirmSignup(@RequestBody ConfirmSignupRequest request) {
        authService.confirmSignup(request);
        return ApiResponse.success("이메일 인증 성공");
    }
}