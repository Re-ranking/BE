package com.example.cv_reranking.auth.service;

import com.example.cv_reranking.auth.dto.*;
import com.example.cv_reranking.auth.entity.Member;
import com.example.cv_reranking.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.client-id}")
    private String clientId;

    @Transactional
    public SignupResponse signup(SignupRequest request, MultipartFile profileImage) {
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

        SignUpResponse cognitoResponse = cognitoClient.signUp(cognitoRequest);

        String profileImageUrl = saveProfileImage(profileImage);

        Member member = memberRepository.save(Member.builder()
                .email(request.getEmail())
                .password("COGNITO")
                .name(request.getName())
                .major(request.getMajor())
                .profileImage(profileImageUrl)
                .cognitoSub(cognitoResponse.userSub())
                .description(request.getDescription())
                .build());

        return SignupResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .major(member.getMajor())
                .profileImage(member.getProfileImage())
                .description(member.getDescription())
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

    private String saveProfileImage(MultipartFile profileImage) {
        if (profileImage == null || profileImage.isEmpty()) {
            return null;
        }

        try {
            String originalFilename = profileImage.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String savedFileName = UUID.randomUUID() + extension;

            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "profile")
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(savedFileName);

            Files.copy(
                    profileImage.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/uploads/profile/" + savedFileName;
        } catch (IOException e) {
            throw new IllegalArgumentException("프로필 이미지 저장에 실패했습니다: " + e.getMessage(), e);
        }
    }
}