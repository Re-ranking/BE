package com.example.cv_reranking.mypage.service;

import com.example.cv_reranking.auth.entity.Member;
import com.example.cv_reranking.auth.repository.MemberRepository;
import com.example.cv_reranking.mypage.dto.MypageCvUpdateRequest;
import com.example.cv_reranking.mypage.dto.MypageProfileUpdateRequest;
import com.example.cv_reranking.mypage.dto.MypageResponse;
import com.example.cv_reranking.mypage.entity.MypageProfile;
import com.example.cv_reranking.mypage.repository.MypageProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.cv_reranking.analysis.entity.CvAnalysisRecord;
import com.example.cv_reranking.analysis.service.CvAnalysisStorageService;
import com.example.cv_reranking.mypage.dto.MypageCvResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final MypageProfileRepository mypageProfileRepository;
    private final MemberRepository memberRepository;
    private final CvAnalysisStorageService cvAnalysisStorageService;

    @Transactional
    public MypageCvResponse getMyCvInfo(Jwt jwt) {
        MypageProfile profile = getOrCreateProfile(jwt);
        CvAnalysisRecord analysis = cvAnalysisStorageService.getLatest(jwt.getSubject());

        if (profile.getSkills().isEmpty() && analysis.getSkills() != null && !analysis.getSkills().isEmpty()) {
            profile.updateCv(
                    analysis.getSkills(),
                    null,
                    null,
                    null
            );
        }

        return MypageCvResponse.from(profile, analysis);
    }

    @Transactional
    public MypageResponse updateProfile(
            Jwt jwt,
            String name,
            String major,
            String introduction,
            MultipartFile profileImage
    ) {
        MypageProfile profile = getOrCreateProfile(jwt);

        String profileImagePath = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            profileImagePath = saveProfileImage(jwt.getSubject(), profileImage);
        }

        profile.updateProfile(
                name,
                major,
                profileImagePath,
                introduction
        );

        return MypageResponse.from(profile);
    }

    @Transactional
    public MypageResponse updateCv(Jwt jwt, MypageCvUpdateRequest request) {
        MypageProfile profile = getOrCreateProfile(jwt);

        profile.updateCv(
                request.skills(),
                request.interests(),
                request.projects(),
                request.awards()
        );

        return MypageResponse.from(profile);
    }

    private String saveProfileImage(String userKey, MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String safeUserKey = userKey.replaceAll("[^a-zA-Z0-9._-]", "_");
            String filename = safeUserKey + "_profile" + extension;

            Path uploadDir = Path.of("uploads", "profile");
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(filename);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/profile/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지를 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    private MypageProfile getOrCreateProfile(Jwt jwt) {
        String userKey = jwt.getSubject();

        return mypageProfileRepository.findByUserKey(userKey)
                .orElseGet(() -> mypageProfileRepository.save(createDefaultProfile(jwt)));
    }

    private MypageProfile createDefaultProfile(Jwt jwt) {
        String email = jwt.getClaimAsString("email");

        Member member = null;
        if (email != null) {
            member = memberRepository.findByEmail(email).orElse(null);
        }

        return MypageProfile.builder()
                .userKey(jwt.getSubject())
                .name(member != null ? member.getName() : jwt.getClaimAsString("name"))
                .major(member != null ? member.getMajor() : "")
                .profileImage(member != null ? member.getProfileImage() : "")
                .introduction(member != null ? member.getDescription() : "")
                .build();
    }
}