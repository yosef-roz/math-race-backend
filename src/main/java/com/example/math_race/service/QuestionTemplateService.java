package com.example.math_race.service;

import com.example.math_race.questionGenerator.QuestionTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class QuestionTemplateService {

    private final ObjectMapper objectMapper;
    private final Map<String, List<QuestionTemplate>> templatesCache;

    @Autowired
    public QuestionTemplateService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.templatesCache = new HashMap<>();
    }

    @PostConstruct
    public void initTemplates() {
        try {

            templatesCache.put("easy", loadFromFile("templates/math-questions/easy.json"));
            templatesCache.put("medium", loadFromFile("templates/math-questions/medium.json"));
            templatesCache.put("hard", loadFromFile("templates/math-questions/hard.json"));

            System.out.println("✅ Question templates loaded successfully!");
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to load question templates on startup", e);
        }
    }


    private List<QuestionTemplate> loadFromFile(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<List<QuestionTemplate>>() {});
        }
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