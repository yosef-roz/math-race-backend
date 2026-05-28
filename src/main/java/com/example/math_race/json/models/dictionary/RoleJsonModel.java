package com.example.math_race.json.models.dictionary;

import com.example.math_race.questionGenerator.tags.enums.RoleType;

import java.util.Set;

public record RoleJsonModel(
        String id,
        String singularMale,
        String pluralMale,
        String singularFemale,
        String pluralFemale,
        RoleType roleType,
        Set<String> validPlaceIds
) {}
