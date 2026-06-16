package com.example.cv_reranking.mypage.dto;

import com.example.cv_reranking.mypage.entity.MypageProfile;
import com.example.cv_reranking.mypage.entity.ProjectItem;

import java.util.List;

public record MypageResponse(
        String name,
        String major,
        String profileImage,
        String introduction,
        List<String> skills,
        List<String> interests,
        List<ProjectItem> projects,
        List<String> awards
) {
    public static MypageResponse from(MypageProfile profile) {
        return new MypageResponse(
                profile.getName(),
                profile.getMajor(),
                profile.getProfileImage(),
                profile.getIntroduction(),
                profile.getSkills(),
                profile.getInterests(),
                profile.getProjects(),
                profile.getAwards()
        );
    }
}