package com.example.math_race.models.dictionary;

import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.ItemCategory;
import com.example.math_race.questionGenerator.tags.enums.PlaceType;

import java.util.Set;

public record PlaceJsonModel(
        String id,
        String singular,
        String plural,
        Gender gender,
        PlaceType placeType,
        Set<ItemCategory> categories
) {}
