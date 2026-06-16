package com.example.cv_reranking.personality.service;

import com.example.cv_reranking.auth.entity.Member;
import com.example.cv_reranking.auth.repository.MemberRepository;
import com.example.cv_reranking.personality.client.PersonalityDlClient;
import com.example.cv_reranking.personality.dto.PersonalitySurveyResponse;
import com.example.cv_reranking.personality.dto.PersonalitySurveySaveRequest;
import com.example.cv_reranking.personality.entity.PersonalitySurvey;
import com.example.cv_reranking.personality.repository.PersonalitySurveyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonalitySurveyService {

    private final PersonalitySurveyRepository personalitySurveyRepository;
    private final MemberRepository memberRepository;
    private final PersonalityDlClient personalityDlClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public PersonalitySurveyResponse saveDraft(String email, PersonalitySurveySaveRequest request) {
        validateStep(request.getStep());

        Member member = getMember(email);
        PersonalitySurvey survey = getOrCreateSurvey(member);

        survey.saveDraft(
                request.getStep(),
                toJsonOrNull(request.getPersonality()),
                toJsonOrNull(request.getCollaborationStyle()),
                toJsonOrNull(request.getLifePattern()),
                toJsonOrNull(request.getCommunication()),
                toJsonOrNull(request.getObjective())
        );

        PersonalitySurvey savedSurvey = personalitySurveyRepository.save(survey);
        return PersonalitySurveyResponse.from(savedSurvey, objectMapper);
    }

    @Transactional
    public PersonalitySurveyResponse submit(String email, PersonalitySurveySaveRequest request) {
        Member member = getMember(email);
        PersonalitySurvey survey = getOrCreateSurvey(member);

        survey.submit(
                toJsonOrNull(request.getPersonality()),
                toJsonOrNull(request.getCollaborationStyle()),
                toJsonOrNull(request.getLifePattern()),
                toJsonOrNull(request.getCommunication()),
                toJsonOrNull(request.getObjective())
        );

        validateAllSectionsFilled(survey);

        PersonalitySurvey savedSurvey = personalitySurveyRepository.save(survey);
        PersonalitySurveyResponse response = PersonalitySurveyResponse.from(savedSurvey, objectMapper);

        try {
            personalityDlClient.sendPersonalitySurvey(response);
            savedSurvey.markDlSyncSuccess();
        } catch (Exception e) {
            savedSurvey.markDlSyncFailed();
        }

        return PersonalitySurveyResponse.from(savedSurvey, objectMapper);
    }

    public PersonalitySurveyResponse getMySurvey(String email) {
        Member member = getMember(email);
        PersonalitySurvey survey = personalitySurveyRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("저장된 성향 설문이 없습니다."));

        return PersonalitySurveyResponse.from(survey, objectMapper);
    }

    private Member getMember(String cognitoSub) {
        return memberRepository.findByCognitoSub(cognitoSub)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    private PersonalitySurvey getOrCreateSurvey(Member member) {
        return personalitySurveyRepository.findByMember(member)
                .orElseGet(() -> PersonalitySurvey.builder()
                        .member(member)
                        .build());
    }

    private void validateStep(Integer step) {
        if (step == null || step < 1 || step > 5) {
            throw new IllegalArgumentException("step은 1부터 5 사이여야 합니다.");
        }
    }

    private void validateAllSectionsFilled(PersonalitySurvey survey) {
        if (survey.getPersonalityJson() == null
                || survey.getCollaborationStyleJson() == null
                || survey.getLifePatternJson() == null
                || survey.getCommunicationJson() == null
                || survey.getObjectiveJson() == null) {
            throw new IllegalArgumentException("모든 성향 테스트 페이지를 저장한 뒤 최종 제출해야 합니다.");
        }
    }

    private String toJsonOrNull(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("성향 설문 응답 변환 실패");
        }
    }
}