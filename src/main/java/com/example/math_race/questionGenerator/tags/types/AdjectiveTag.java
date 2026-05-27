package com.example.math_race.questionGenerator.tags.types;

import com.example.math_race.entities.dictionary.AdjectiveEntity;
import com.example.math_race.questionGenerator.tags.core.MatchableTag;
import com.example.math_race.questionGenerator.tags.enums.AdjectiveType;
import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.Plurality;

import java.util.HashMap;
import java.util.Map;

import static com.example.math_race.questionGenerator.tags.enums.Gender.MALE;
import static com.example.math_race.questionGenerator.tags.enums.Plurality.SINGULAR;


public class AdjectiveTag implements MatchableTag {
    private String id;
    private AdjectiveType type;
    private Map<String, String> forms = new HashMap<>();

    public AdjectiveTag(String id, AdjectiveType type) {
        this.id = id;
        this.type = type;
    }

    public AdjectiveTag(AdjectiveEntity entity) {
        id = entity.getAdjectiveId();
        type = entity.getType();
        forms = entity.getFormsAsMap();
    }

    @Override
    public String getProperty(String key) {
        if (key == null || key.isEmpty()) {
            return getWord(MALE, SINGULAR);
        }

        if (key.equals("id")) return id;
        if (key.equals("type") || key.equals("t")) return type.toString();

        String value = forms.get(key);
        if (value != null) {
            return value;
        }

        String upperKey = key.toUpperCase();
        String newKey = switch (upperKey) {
            case "M_S" -> "MALE_SINGULAR";
            case "M_P" -> "MALE_PLURAL";
            case "F_S" -> "FEMALE_SINGULAR";
            case "F_P" -> "FEMALE_PLURAL";
            default -> upperKey;
        };

        return forms.getOrDefault(newKey, id);
    }

//    @Override
//    public String getProperty(String key) {
//        if (key == null || key.isEmpty()) {
//            return getWord(MALE,"s");
//        }
//
//        if (key.equals("id")) {
//            return id;
//        }
//        if (key.equals("type") || key.equals("t")) {
//            return type.toString();
//        }
//
//        return forms.getOrDefault(key.trim().toUpperCase(), id);
//    }

//    public void addForm(Gender gender, String number, String word) {
//        String key = gender.name() + "_" + number.toUpperCase();
//        forms.put(key, word);
//    }

    public void addForm(Gender gender, Plurality plurality, String word) {
        String key = gender.name() + "_" + plurality.name();
        forms.put(key, word);
    }

    public String getWord(Gender gender, Plurality plurality) {
        String key = gender.name() + "_" + plurality.name();
        return forms.getOrDefault(key, id);
    }

//    public String getWord(Gender gender, String number) {
//        String key = gender.name() + "_" + number.toUpperCase();
//        return forms.getOrDefault(key, id);
//    }

    public boolean matches(Map<String, String> constraints) {
        if (constraints.containsKey("type") && !constraints.get("type").equals("?")) {
            String reqType = constraints.get("type").trim().toUpperCase();

            try {
                AdjectiveType requestedType = AdjectiveType.valueOf(reqType);
                if (this.type != requestedType) return false;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        if (constraints.containsKey("id") && !constraints.get("id").equals("?")) {
            String reqId = constraints.get("id").trim();
            if (reqId.startsWith("!")) {
                if (this.id.equalsIgnoreCase(reqId.substring(1))) return false;
            } else {
                if (!this.id.equalsIgnoreCase(reqId)) return false;
            }
        }

        return true;
    }
}
