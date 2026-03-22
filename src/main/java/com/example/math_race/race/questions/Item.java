package com.example.math_race.race.questions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item implements QuestionEntity {
    private String singular;
    private String plural;
    private Gender gender;
    private Set<Category> categories;

    @Override
    public String getProperty(String key) {
        if ("s".equals(key)) return singular;
        if ("p".equals(key)) return plural;
        if ("g".equals(key)) return gender.name();
        return plural;
    }

    public boolean matches(Map<String, String> constraints) {
        if (constraints.containsKey("type") && !constraints.get("type").equals("?")) {
            try {
                Category reqCategory = Category.valueOf(constraints.get("type").trim().toUpperCase());
                if (!this.categories.contains(reqCategory)) return false;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        if (constraints.containsKey("s") && !constraints.get("s").equals("?")) {
            if (!this.singular.equalsIgnoreCase(constraints.get("s").trim())) return false;
        }

        if (constraints.containsKey("p") && !constraints.get("p").equals("?")) {
            if (!this.plural.equalsIgnoreCase(constraints.get("p").trim())) return false;
        }

        if (constraints.containsKey("g") && !constraints.get("g").equals("?")) {
            String reqGender = constraints.get("g").trim().toLowerCase();
            if (reqGender.equals("m") && this.gender != Gender.MALE) return false;
            if (reqGender.equals("f") && this.gender != Gender.FEMALE) return false;
        }

        return true;
    }
}