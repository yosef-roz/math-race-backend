package com.example.math_race.models.dictionary;

import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.ItemCategory;
import com.example.math_race.questionGenerator.tags.enums.UnitType;

import java.util.Set;

public record ItemJsonModel(
        String id,
        String singular,
        String plural,
        Gender gender,
        Set<UnitType> allowedUnits,
        Set<ItemCategory> categories
) {}
