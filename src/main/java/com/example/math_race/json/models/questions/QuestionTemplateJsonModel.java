package com.example.math_race.json.models.questions;

import java.util.List;

public record QuestionTemplateJsonModel(
        String id,
        String questionTemplate,
        String answerTemplate,
        String hintTemplate,
        List<String> distractorsTemplates
) {
}
