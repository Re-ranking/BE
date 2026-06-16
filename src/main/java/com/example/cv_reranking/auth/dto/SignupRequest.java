package com.example.cv_reranking.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청")
public class SignupRequest {

    @Schema(description = "이메일", example = "test@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "비밀번호", example = "Password123!")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Schema(description = "전공", example = "소프트웨어융합공학")
    @NotBlank(message = "전공은 필수입니다.")
    private String major;

    @Schema(description = "한줄 소개", example = "협업과 데이터 분석에 관심이 많은 학생입니다.")
    @Size(max = 100, message = "한줄 소개는 100자 이하로 입력해주세요.")
    private String description;
}