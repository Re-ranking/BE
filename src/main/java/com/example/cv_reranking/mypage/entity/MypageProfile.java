package com.example.cv_reranking.mypage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "mypage_profiles")
public class MypageProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userKey;

    private String name;

    private String major;

    @Column(columnDefinition = "TEXT")
    private String profileImage;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mypage_skills", joinColumns = @JoinColumn(name = "mypage_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mypage_interests", joinColumns = @JoinColumn(name = "mypage_id"))
    @Column(name = "interest", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> interests = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mypage_awards", joinColumns = @JoinColumn(name = "mypage_id"))
    @Column(name = "award", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> awards = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mypage_projects", joinColumns = @JoinColumn(name = "mypage_id"))
    @Builder.Default
    private List<ProjectItem> projects = new ArrayList<>();

    public void updateProfile(String name, String major, String profileImage, String introduction) {
        if (name != null) this.name = name;
        if (major != null) this.major = major;
        if (profileImage != null) this.profileImage = profileImage;
        if (introduction != null) this.introduction = introduction;
    }

    public void updateCv(List<String> skills, List<String> interests, List<ProjectItem> projects, List<String> awards) {
        if (skills != null) this.skills = new ArrayList<>(skills);
        if (interests != null) this.interests = new ArrayList<>(interests);
        if (projects != null) this.projects = new ArrayList<>(projects);
        if (awards != null) this.awards = new ArrayList<>(awards);
    }

    public void updateSkills(List<String> skills) {
        this.skills.clear();

        if (skills != null) {
            this.skills.addAll(skills);
        }
    }
}