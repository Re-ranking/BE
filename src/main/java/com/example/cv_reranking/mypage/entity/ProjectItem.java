package com.example.cv_reranking.mypage.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Embeddable
public class ProjectItem {

    private String period;

    private String title;

    private String description;
}