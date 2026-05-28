package com.example.math_race.service;

import com.example.math_race.entities.templates.QuestionTemplateEntity;
import com.example.math_race.questionGenerator.question.QuestionTemplate;
import com.example.math_race.repositories.QuestionTemplatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class QuestionTemplateService {

    private final QuestionTemplatesRepository questionRepository;
    private Map<String, List<QuestionTemplate>> templatesCache;

    @Autowired
    public QuestionTemplateService(QuestionTemplatesRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @PostConstruct
    public void initTemplates() {
        List<QuestionTemplateEntity> entities = questionRepository.loadAllTemplates();

        this.templatesCache = entities.stream()
                .map(QuestionTemplate::new)
                .collect(Collectors.groupingBy(
                        this::extractDifficulty,
                        Collectors.toList()
                ));
    }

    private String extractDifficulty(QuestionTemplate template) {
        String id = template.id();
        if (id != null && id.contains("_")) {
            return id.split("_")[0].toLowerCase();
        }
        return "general";
    }

    public List<QuestionTemplate> getTemplatesByDifficulty(String difficulty) {
        return templatesCache.getOrDefault(difficulty.toLowerCase(), List.of());
    }

    public QuestionTemplate getTemplateByDifficulty(String difficulty) {
        List<QuestionTemplate> templates = getTemplatesByDifficulty(difficulty);

        if (templates == null || templates.isEmpty()) {
            return null;
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(templates.size());
        return templates.get(randomIndex);
    }
}
