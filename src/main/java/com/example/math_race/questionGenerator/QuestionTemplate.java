package com.example.math_race.questionGenerator;

import java.util.List;

public record QuestionTemplate(
        String id,
        String questionTemplate,
        String answerTemplate,
        String hintTemplate,
        List<String> distractorsTemplates
) {}