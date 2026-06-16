package com.example.cv_reranking.personality.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "성향 테스트 임시 저장/최종 제출 요청")
public class PersonalitySurveySaveRequest {

    @Schema(example = "1", description = "현재 저장하는 페이지 번호입니다. Personality=1, CollaborationStyle=2, LifePattern=3, Communication=4, Objective=5")
    private Integer step;

    @Schema(description = "개인 성향 응답입니다. 각 문항은 '아니다', '보통', '그렇다' 중 하나를 선택합니다.")
    private Personality personality;

    @Schema(description = "협업 스타일 응답입니다.")
    private CollaborationStyle collaborationStyle;

    @Schema(description = "생활 패턴 응답입니다.")
    private LifePattern lifePattern;

    @Schema(description = "소통 방식 응답입니다.")
    private Communication communication;

    @Schema(description = "참여 목적 및 목표 응답입니다.")
    private Objective objective;

    @Getter
    @NoArgsConstructor
    @Schema(description = "Personality 페이지 응답")
    public static class Personality {

        @Schema(
                description = "팀에서 먼저 의견을 내고 일을 시작하는 편인지에 대한 응답입니다.",
                allowableValues = {"아니다", "보통", "그렇다"},
                example = "아니다"
        )
        private String startInitiative;

        @Schema(
                description = "맡은 일을 끝까지 수행하고 마감에 맞추는 성향인지에 대한 응답입니다.",
                allowableValues = {"아니다", "보통", "그렇다"},
                example = "보통"
        )
        private String completionTendency;

        @Schema(
                description = "예상치 못한 변화나 새로운 상황에 유연하게 대응하는지에 대한 응답입니다.",
                allowableValues = {"아니다", "보통", "그렇다"},
                example = "그렇다"
        )
        private String adaptability;

        @Schema(
                description = "새로운 주제나 어려운 문제를 피하지 않고 시도하는지에 대한 응답입니다.",
                allowableValues = {"아니다", "보통", "그렇다"},
                example = "그렇다"
        )
        private String challengeOrientation;

        @Schema(
                description = "꾸준하게 일하고 감정 기복 없이 일정하게 참여하는지에 대한 응답입니다.",
                allowableValues = {"아니다", "보통", "그렇다"},
                example = "보통"
        )
        private String consistency;

        @Schema(
                description = "압박 상황에서 침착하게 해결하는 편인지, 부담을 크게 느끼는지에 대한 응답입니다.",
                allowableValues = {"아니다", "보통", "그렇다"},
                example = "아니다"
        )
        private String pressureHandling;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "Collaboration style 페이지 응답")
    public static class CollaborationStyle {

        @ArraySchema(
                arraySchema = @Schema(description = "역할 선호 (2개 선택)"),
                minItems = 2,
                maxItems = 2,
                schema = @Schema(allowableValues = {
                        "리더/조율자",
                        "디자인 담당",
                        "발표/문서화 담당",
                        "기획자",
                        "아이디어 제안자",
                        "자료조사 담당",
                        "개발/구현 담당",
                        "보조/지원 역할"
                })
        )
        private List<String> rolePreference;

        @Schema(
                description = "업무 수행 방식 (1개 선택)",
                allowableValues = {
                        "개인 작업 선호",
                        "분업 후 공유 선호",
                        "실시간 협업 선호"
                },
                example = "분업 후 공유 선호"
        )
        private String workStyle;

        @Schema(
                description = "의사결정 스타일 (1개 선택)",
                allowableValues = {
                        "빠르게 결정하고 실행",
                        "충분히 논의 후 결정",
                        "근거와 자료 기반 결정",
                        "리더 중심 결정 선호"
                },
                example = "충분히 논의 후 결정"
        )
        private String decisionStyle;

        @ArraySchema(
                arraySchema = @Schema(description = "팀 기여 방식 (2개 선택)"),
                minItems = 2,
                maxItems = 2,
                schema = @Schema(allowableValues = {
                        "아이디어 제시",
                        "실행력",
                        "일정 관리",
                        "분위기 조율",
                        "자료 정리",
                        "발표/전달력"
                })
        )
        private List<String> contributionStyle;

        @Schema(
                description = "갈등 상황 대처 방식 (1개 선택)",
                allowableValues = {
                        "직접 대화로 해결",
                        "중재자 필요",
                        "일단 피하고 나중에 이야기",
                        "다수 의견 따름"
                },
                example = "중재자 필요"
        )
        private String conflictHandling;

        @Schema(
                description = "협업 선호 강도 (1개 선택)",
                allowableValues = {
                        "매우 협업형",
                        "혼합형",
                        "독립형"
                },
                example = "혼합형"
        )
        private String cooperationLevel;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "Life Pattern 페이지 응답")
    public static class LifePattern {

        @ArraySchema(
                arraySchema = @Schema(description = "활동 시간대 (2개 선택)"),
                minItems = 2,
                maxItems = 2,
                schema = @Schema(allowableValues = {
                        "아침형",
                        "낮형",
                        "저녁형",
                        "새벽형"
                })
        )
        private List<String> activityTime;

        @ArraySchema(
                arraySchema = @Schema(description = "작업 가능 시간 (2개 선택)"),
                minItems = 2,
                maxItems = 2,
                schema = @Schema(allowableValues = {
                        "평일 오전",
                        "평일 오후",
                        "평일 저녁",
                        "주말 위주",
                        "시간 유동적"
                })
        )
        private List<String> availableTime;

        @Schema(
                description = "일정 관리 스타일 (1개 선택)",
                allowableValues = {
                        "계획형",
                        "반계획형",
                        "즉흥형"
                },
                example = "즉흥형"
        )
        private String scheduleManagementStyle;

        @Schema(
                description = "마감 처리 방식 (1개 선택)",
                allowableValues = {
                        "미리 준비형",
                        "중간 점검형",
                        "마감 집중형"
                },
                example = "중간 점검형"
        )
        private String deadlineHandlingStyle;

        @Schema(
                description = "회의 가능 빈도 (1개 선택)",
                allowableValues = {
                        "주 1회",
                        "주 2~3회",
                        "필요할 때만 가능",
                        "온라인 회의 선호"
                },
                example = "주 2~3회"
        )
        private String meetingFrequency;

        @Schema(
                description = "응답 가능 속도 (1개 선택)",
                allowableValues = {
                        "1시간 이내",
                        "반나절 이내",
                        "하루 이내",
                        "불규칙"
                },
                example = "반나절 이내"
        )
        private String responseSpeed;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "Communication 페이지 응답")
    public static class Communication {

        @Schema(
                description = "소통 빈도 선호 (1개 선택)",
                allowableValues = {
                        "자주 소통 선호",
                        "적당한 소통 선호",
                        "최소 소통 선호"
                },
                example = "적당한 소통 선호"
        )
        private String communicationFrequency;

        @ArraySchema(
                arraySchema = @Schema(description = "소통 채널 선호 (중복 선택)"),
                schema = @Schema(allowableValues = {
                        "카톡/메신저",
                        "디스코드/슬랙",
                        "전화/음성통화",
                        "대면 회의",
                        "문서 기반 정리"
                })
        )
        private List<String> channelPreference;

        @Schema(
                description = "피드백 스타일 (1개 선택)",
                allowableValues = {
                        "직설적인 피드백",
                        "부드럽고 조심스러운 피드백",
                        "구체적 근거 중심 피드백",
                        "문서로 정리된 피드백"
                },
                example = "문서로 정리된 피드백"
        )
        private String feedbackStyle;

        @Schema(
                description = "의견 표현 방식 (1개 선택)",
                allowableValues = {
                        "즉시 표현형",
                        "숙고 후 표현형",
                        "상대 분위기 보고 표현형"
                },
                example = "숙고 후 표현형"
        )
        private String opinionExpressionStyle;

        @ArraySchema(
                arraySchema = @Schema(description = "회의 스타일 (중복 선택)"),
                schema = @Schema(allowableValues = {
                        "자유롭게 브레인스토밍",
                        "안건 정리 후 짧고 효율적으로",
                        "리더 중심 진행",
                        "자료 기반 논리"
                })
        )
        private List<String> meetingStyle;

        @Schema(
                description = "갈등 커뮤니케이션 방식 (1개 선택)",
                allowableValues = {
                        "바로 이야기 하기",
                        "시간을 두고 이야기",
                        "글로 정리해서 이야기",
                        "중재자를 통한 이야기"
                },
                example = "시간을 두고 이야기"
        )
        private String conflictCommunicationStyle;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "Objective 페이지 응답")
    public static class Objective {

        @ArraySchema(
                arraySchema = @Schema(description = "참여 목적 (중복 선택)"),
                schema = @Schema(allowableValues = {
                        "수상",
                        "포트폴리오 강화",
                        "실무 경험",
                        "취업 준비",
                        "팀 프로젝트 경험",
                        "인맥 형성",
                        "진로 탐색"
                })
        )
        private List<String> participationPurpose;

        @Schema(
                description = "목표 수준 (1개 선택)",
                allowableValues = {
                        "수상 목표",
                        "본선 진출 목표",
                        "결과보다 완성 목표",
                        "경험 자체가 중요"
                },
                example = "본선 진출 목표"
        )
        private String goalLevel;

        @Schema(
                description = "몰입 가능 수준 (1개 선택)",
                allowableValues = {
                        "매우 높음",
                        "중간",
                        "제한적"
                },
                example = "중간"
        )
        private String commitmentLevel;

        @ArraySchema(
                arraySchema = @Schema(description = "선호 공모전 유형 (중복 선택)"),
                schema = @Schema(allowableValues = {
                        "기획",
                        "개발",
                        "디자인",
                        "데이터/AI",
                        "창업/비즈니스",
                        "발표 중심"
                })
        )
        private List<String> preferredCompetitionType;

        @Schema(
                description = "장기/단기 프로젝트 선호 (1개 선택)",
                allowableValues = {
                        "단기 집중",
                        "중기",
                        "장기"
                },
                example = "중기"
        )
        private String projectDurationPreference;

        @Schema(
                description = "기대하는 팀 분위기 (1개 선택)",
                allowableValues = {
                        "빡세게 성과내는 분위기",
                        "서로 배려하면서 가는 분위기",
                        "자유롭고 유연한 분위기",
                        "체계적이고 규칙적인 분위기"
                },
                example = "서로 배려하면서 가는 분위기"
        )
        private String desiredTeamMood;
    }
}