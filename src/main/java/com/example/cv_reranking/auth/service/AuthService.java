package com.example.cv_reranking.auth.service;

import com.example.cv_reranking.auth.dto.*;
import com.example.cv_reranking.auth.entity.Member;
import com.example.cv_reranking.auth.repository.MemberRepository;
import com.example.cv_reranking.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일");
        }

        Member member = memberRepository.save(Member.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .major(request.getMajor())
                .profileImage(request.getProfileImage())
                .build());

        return toResponse(member, null);
    }

    // 인증만 수행
    public Member authenticate(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("로그인 실패"));

        if (!member.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("로그인 실패");
        }

        return member;
    }

    // JWT 발급용
    public String issueToken(Member member) {
        return jwtUtil.createToken(member.getId(), member.getEmail());
    }

    public AuthResponse toResponse(Member member, String token) {
        return AuthResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .major(member.getMajor())
                // token 필드는 나중에 추가해도 됨
                .build();
    }
}