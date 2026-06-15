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

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CompetitionDataLoader implements ApplicationRunner {

    private final CompetitionRepository competitionRepository;
    private final ObjectMapper objectMapper;

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (competitionRepository.count() > 0) {
            return;
        }

        ClassPathResource resource = new ClassPathResource("contest_normalize.json");

        if (!resource.exists()) {
            return;
        }

        List<CompetitionSeedRow> rows = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<List<CompetitionSeedRow>>() {}
        );

        List<Competition> competitions = rows.stream()
                .map(row -> Competition.builder()
                        .dlContestId(row.getContestId())
                        .name(row.getTitle())
                        .sourceUrl(row.getSourceUrl())
                        .category(join(row.getDomains()))
                        .domains(join(row.getDomains()))
                        .skills(join(row.getSkills()))
                        .applicationTarget(row.getTarget())
                        .organizer(row.getHost())
                        .applicationPeriod(row.getPeriod())
                        .applicationEndDate(parseLastDate(row.getPeriod()))
                        .totalPrize(row.getTotalPrize())
                        .firstPrize(row.getFirstPrize())
                        .homepage(row.getHomepage())
                        .representativeImageUrl(row.getImageUrl())
                        .description(row.getDescription())
                        .build())
                .toList();

        competitionRepository.saveAll(competitions);
    }

    private String join(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        return String.join(", ", values);
    }

    private LocalDate parseLastDate(String applicationPeriod) {
        if (applicationPeriod == null || applicationPeriod.isBlank()) {
            return null;
        }

        Matcher matcher = DATE_PATTERN.matcher(applicationPeriod);

        LocalDate lastDate = null;

        while (matcher.find()) {
            lastDate = LocalDate.parse(matcher.group());
        }

        return lastDate;
    }
}