package com.example.math_race.json.models.dictionary;

import com.example.math_race.questionGenerator.tags.enums.AdjectiveType;
import java.util.Map;

public record AdjectiveJsonModel(
        String id,
        AdjectiveType type,
        Map<String, String> forms
) {}
