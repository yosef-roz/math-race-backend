package com.example.math_race.questionGenerator.tags.types;

import com.example.math_race.questionGenerator.tags.core.QuestionEntity;
import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.ItemCategory;
import com.example.math_race.questionGenerator.tags.enums.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceTag implements QuestionEntity {
    private String id;
    private String singular;
    private String plural;
    private Gender gender;
    private PlaceType placeType;
    private Set<ItemCategory> categories;


    public PlaceTag(String id, String singular, String plural, Gender gender, PlaceType placeType, ItemCategory... categories) {
        this.id = id;
        this.singular = singular;
        this.plural = plural;
        this.gender = gender;
        this.placeType = placeType;
        this.categories = new HashSet<>(Arrays.asList(categories));
    }

    @Override
    public String getProperty(String key) {
        if ("id".equals(key)) return id;
        if ("s".equals(key)) return singular;
        if ("p".equals(key)) return plural;
        if ("g".equals(key)) return gender.toString();

        if ("t".equalsIgnoreCase(key)) {
            return categories.stream()
                    .map(Enum::name)
                    .collect(java.util.stream.Collectors.joining("|"));
        }

        if ("pt".equals(key) || "place_type".equals(key)) return placeType.name();

        if ("he_she".equals(key)) {
            return gender == Gender.MALE ? "הוא" : "היא";
        }
        if ("his_hers".equals(key)) {
            return gender == Gender.MALE ? "שלו" : "שלה";
        }
        if ("to_him_her".equals(key)) {
            return gender == Gender.MALE ? "לו" : "לה";
        }

        if ("in_it".equals(key)) {
            return gender == Gender.MALE ? "בו" : "בה";
        }
        if ("to_it".equals(key)) {
            return gender == Gender.MALE ? "אליו" : "אליה";
        }
        if ("from_it".equals(key)) {
            return gender == Gender.MALE ? "ממנו" : "ממנה";
        }

        return singular;
    }

    public boolean matches(Map<String, String> constraints) {
        if (constraints.containsKey("id") && !constraints.get("id").equals("?")) {
            String reqId = constraints.get("id").trim();

            if (reqId.startsWith("!")) {
                String excludedId = reqId.substring(1);

                if (this.id.equalsIgnoreCase(excludedId)) return false;
            } else {
                if (!this.id.equalsIgnoreCase(reqId)) return false;
            }
        }

        if (constraints.containsKey("place_type") && !constraints.get("place_type").equals("?")) {
            String req = constraints.get("place_type").trim().toUpperCase();
            boolean isNot = req.startsWith("!");
            if (isNot) req = req.substring(1);

            // ב-PlaceType בדרך כלל יש רק ערך אחד למקום, אז נתמוך ב-OR (|) בלבד
            String[] allowedTypes = req.split("\\|");
            boolean foundMatch = false;
            for (String t : allowedTypes) {
                if (this.placeType.name().equals(t.trim())) {
                    foundMatch = true;
                    break;
                }
            }

            if (isNot == foundMatch) return false;
        }

        if (constraints.containsKey("type") && !constraints.get("type").equals("?")) {

            String rawExpr = constraints.get("type").trim().toUpperCase();
            boolean isNegated = rawExpr.startsWith("!");

            if (isNegated) {
                rawExpr = rawExpr.substring(1);
            }


            String[] orGroups = rawExpr.split("\\|");
            boolean expressionResult = false;

            for (String group : orGroups) {
                String[] andRequirements = group.split("&");
                boolean allInGroupMatch = true;

                for (String req : andRequirements) {
                    try {
                        ItemCategory cat = ItemCategory.valueOf(req.trim());
                        if (!this.categories.contains(cat)) {
                            allInGroupMatch = false;
                            break;
                        }
                    } catch (IllegalArgumentException e) {
                        allInGroupMatch = false;
                        break;
                    }
                }

                if (allInGroupMatch) {
                    expressionResult = true;
                    break;
                }
            }

            return isNegated != expressionResult;
        }

        return true;
    }

}
