package com.example.cv_reranking.auth.controller;

import com.example.cv_reranking.auth.dto.*;
import com.example.cv_reranking.auth.entity.Member;
import com.example.cv_reranking.auth.service.AuthService;
import com.example.cv_reranking.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<SignupResponse> signup(@RequestBody SignupRequest request) {
        return ApiResponse.success("회원가입 성공", authService.signup(request));
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