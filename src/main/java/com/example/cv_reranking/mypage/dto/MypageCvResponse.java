package com.example.cv_reranking.mypage.dto;

import com.example.cv_reranking.analysis.entity.CvAnalysisRecord;
import com.example.cv_reranking.analysis.entity.CvAnalysisScoreItem;
import com.example.cv_reranking.mypage.entity.MypageProfile;
import com.example.cv_reranking.mypage.entity.ProjectItem;

import java.util.List;

public record MypageCvResponse(
        String name,
        String major,
        String profileImage,
        String introduction,
        List<String> skills,
        List<String> primaryDomains,
        List<ScoreItem> strengths,
        List<ScoreItem> weaknesses,
        List<String> interests,
        List<ProjectItem> projects,
        List<String> awards
) {
    public static MypageCvResponse from(MypageProfile profile, CvAnalysisRecord analysis) {
        return new MypageCvResponse(
                profile.getName(),
                profile.getMajor(),
                profile.getProfileImage(),
                profile.getIntroduction(),
                analysis.getSkills(),
                analysis.getPrimaryDomains(),
                analysis.getStrengths().stream().map(ScoreItem::from).toList(),
                analysis.getWeaknesses().stream().map(ScoreItem::from).toList(),
                profile.getInterests(),
                profile.getProjects(),
                profile.getAwards()
        );
    }

    public record ScoreItem(String name, int score) {
        public static ScoreItem from(CvAnalysisScoreItem item) {
            return new ScoreItem(item.getName(), item.getScore());
        }
    }
}