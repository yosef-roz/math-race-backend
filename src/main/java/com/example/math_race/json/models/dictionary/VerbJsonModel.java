package com.example.math_race.json.models.dictionary;


import java.util.Map;

public record VerbJsonModel(
        String id,
        Map<String, String> forms
) {}
