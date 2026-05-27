package com.example.math_race.models.dictionary;

import com.example.math_race.questionGenerator.tags.enums.Gender;

public record HumanJsonModel(
        String name,
        Gender gender
) {}
