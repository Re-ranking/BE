package com.example.cv_reranking.auth.service;

import com.example.cv_reranking.auth.dto.*;
import com.example.cv_reranking.auth.entity.Member;
import com.example.cv_reranking.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.client-id}")
    private String clientId;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일");
        }

        SignUpRequest cognitoRequest = SignUpRequest.builder()
                .clientId(clientId)
                .username(request.getEmail())
                .password(request.getPassword())
                .userAttributes(
                        AttributeType.builder()
                                .name("email")
                                .value(request.getEmail())
                                .build()
                )
                .build();

        cognitoClient.signUp(cognitoRequest);

        Member member = memberRepository.save(Member.builder()
                .email(request.getEmail())
                .password("COGNITO")
                .name(request.getName())
                .major(request.getMajor())
                .profileImage(request.getProfileImage())
                .build());

        return SignupResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .major(member.getMajor())
                .profileImage(member.getProfileImage())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .clientId(clientId)
                .authParameters(Map.of(
                        "USERNAME", request.getEmail(),
                        "PASSWORD", request.getPassword()
                ))
                .build();

        InitiateAuthResponse response = cognitoClient.initiateAuth(authRequest);

        return LoginResponse.builder()
                .accessToken(response.authenticationResult().accessToken())
                .build();
    }

    public void confirmSignup(ConfirmSignupRequest request) {
        ConfirmSignUpRequest confirmRequest = ConfirmSignUpRequest.builder()
                .clientId(clientId)
                .username(request.getEmail())
                .confirmationCode(request.getCode())
                .build();

        cognitoClient.confirmSignUp(confirmRequest);
    }

    public void logout(String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        GlobalSignOutRequest request = GlobalSignOutRequest.builder()
                .accessToken(accessToken)
                .build();

        cognitoClient.globalSignOut(request);
    }
}