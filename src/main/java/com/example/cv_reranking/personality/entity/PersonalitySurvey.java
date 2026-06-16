package com.example.cv_reranking.personality.entity;

import com.example.cv_reranking.auth.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalitySurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PersonalitySurveyStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DlSyncStatus dlSyncStatus;

    @Column(nullable = false)
    private Integer currentStep;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String personalityJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String collaborationStyleJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String lifePatternJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String communicationJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String objectiveJson;

    private LocalDateTime submittedAt;

    private LocalDateTime lastDlSyncedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private PersonalitySurvey(Member member) {
        this.member = member;
        this.status = PersonalitySurveyStatus.DRAFT;
        this.dlSyncStatus = DlSyncStatus.NOT_REQUESTED;
        this.currentStep = 0;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void saveDraft(
            Integer step,
            String personalityJson,
            String collaborationStyleJson,
            String lifePatternJson,
            String communicationJson,
            String objectiveJson
    ) {
        mergeSections(personalityJson, collaborationStyleJson, lifePatternJson, communicationJson, objectiveJson);
        this.currentStep = Math.max(this.currentStep, step);
        this.status = PersonalitySurveyStatus.DRAFT;
    }

    public void submit(
            String personalityJson,
            String collaborationStyleJson,
            String lifePatternJson,
            String communicationJson,
            String objectiveJson
    ) {
        mergeSections(personalityJson, collaborationStyleJson, lifePatternJson, communicationJson, objectiveJson);
        this.currentStep = 5;
        this.status = PersonalitySurveyStatus.SUBMITTED;
        this.dlSyncStatus = DlSyncStatus.PENDING;
        this.submittedAt = LocalDateTime.now();
    }

    public void markDlSyncSuccess() {
        this.dlSyncStatus = DlSyncStatus.SUCCESS;
        this.lastDlSyncedAt = LocalDateTime.now();
    }

    public void markDlSyncFailed() {
        this.dlSyncStatus = DlSyncStatus.FAILED;
    }

    private void mergeSections(
            String personalityJson,
            String collaborationStyleJson,
            String lifePatternJson,
            String communicationJson,
            String objectiveJson
    ) {
        if (personalityJson != null) this.personalityJson = personalityJson;
        if (collaborationStyleJson != null) this.collaborationStyleJson = collaborationStyleJson;
        if (lifePatternJson != null) this.lifePatternJson = lifePatternJson;
        if (communicationJson != null) this.communicationJson = communicationJson;
        if (objectiveJson != null) this.objectiveJson = objectiveJson;
    }
}