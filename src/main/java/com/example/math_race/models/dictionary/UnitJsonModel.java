package com.example.math_race.models.dictionary;

import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.ItemCategory;
import com.example.math_race.questionGenerator.tags.enums.UnitType;

import java.util.Set;

public record UnitJsonModel(
        String id,
        String singular,
        String plural,
        Gender gender,
        UnitType type,
        Set<ItemCategory>validItemCategories
) {
}
