package com.example.cv_reranking.competition.loader;

import com.example.cv_reranking.competition.entity.Competition;
import com.example.cv_reranking.competition.repository.CompetitionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompetitionDataLoader implements ApplicationRunner {

    private final CompetitionRepository competitionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (competitionRepository.count() > 0) {
            return;
        }

        ClassPathResource resource = new ClassPathResource("contests_result.json");

        if (!resource.exists()) {
            return;
        }

        List<CompetitionSeedRow> rows = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<List<CompetitionSeedRow>>() {}
        );

        List<Competition> competitions = rows.stream()
                .map(row -> Competition.builder()
                        .name(row.getName())
                        .sourceUrl(row.getSourceUrl())
                        .category(row.getCategory())
                        .applicationTarget(row.getApplicationTarget())
                        .organizer(row.getOrganizer())
                        .applicationPeriod(row.getApplicationPeriod())
                        .totalPrize(row.getTotalPrize())
                        .firstPrize(row.getFirstPrize())
                        .homepage(row.getHomepage())
                        .representativeImageUrl(row.getRepresentativeImageUrl())
                        .build())
                .toList();

        competitionRepository.saveAll(competitions);
    }
}