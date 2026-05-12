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
    public ApiResponse<AuthResponse> signup(@RequestBody SignupRequest request) {
        return ApiResponse.success("회원가입 성공", authService.signup(request));
    }

    // 지금: 세션 방식
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @RequestBody LoginRequest request,
            HttpSession session
    ) {
        Member member = authService.authenticate(request);

        session.setAttribute("memberId", member.getId());

        return ApiResponse.success("로그인 성공",
                authService.toResponse(member, null));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success("로그아웃 성공");
    }
}