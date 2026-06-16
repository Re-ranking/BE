package com.example.cv_reranking.personality.dto;

import com.example.cv_reranking.personality.entity.DlSyncStatus;
import com.example.cv_reranking.personality.entity.PersonalitySurvey;
import com.example.cv_reranking.personality.entity.PersonalitySurveyStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PersonalitySurveyResponse {

    private Long surveyId;
    private Long memberId;
    private PersonalitySurveyStatus status;
    private DlSyncStatus dlSyncStatus;
    private Integer currentStep;

    private PersonalitySurveySaveRequest.Personality personality;
    private PersonalitySurveySaveRequest.CollaborationStyle collaborationStyle;
    private PersonalitySurveySaveRequest.LifePattern lifePattern;
    private PersonalitySurveySaveRequest.Communication communication;
    private PersonalitySurveySaveRequest.Objective objective;

    private LocalDateTime submittedAt;

    public static PersonalitySurveyResponse from(PersonalitySurvey survey, ObjectMapper objectMapper) {
        return PersonalitySurveyResponse.builder()
                .surveyId(survey.getId())
                .memberId(survey.getMember().getId())
                .status(survey.getStatus())
                .dlSyncStatus(survey.getDlSyncStatus())
                .currentStep(survey.getCurrentStep())
                .personality(readValue(
                        survey.getPersonalityJson(),
                        objectMapper,
                        PersonalitySurveySaveRequest.Personality.class
                ))
                .collaborationStyle(readValue(
                        survey.getCollaborationStyleJson(),
                        objectMapper,
                        PersonalitySurveySaveRequest.CollaborationStyle.class
                ))
                .lifePattern(readValue(
                        survey.getLifePatternJson(),
                        objectMapper,
                        PersonalitySurveySaveRequest.LifePattern.class
                ))
                .communication(readValue(
                        survey.getCommunicationJson(),
                        objectMapper,
                        PersonalitySurveySaveRequest.Communication.class
                ))
                .objective(readValue(
                        survey.getObjectiveJson(),
                        objectMapper,
                        PersonalitySurveySaveRequest.Objective.class
                ))
                .submittedAt(survey.getSubmittedAt())
                .build();
    }

    private static <T> T readValue(String json, ObjectMapper objectMapper, Class<T> clazz) {
        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("성향 설문 응답 변환 실패: " + clazz.getSimpleName(), e);
        }
    }
}