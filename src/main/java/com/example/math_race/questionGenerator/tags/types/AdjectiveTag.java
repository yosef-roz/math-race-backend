package com.example.math_race.questionGenerator.tags.types;

import com.example.math_race.questionGenerator.tags.enums.AdjectiveType;

import java.util.HashMap;
import java.util.Map;



public class AdjectiveTag {
    private String id;
    private AdjectiveType type; // שינינו ל-Enum!
    private Map<String, String> forms = new HashMap<>();

    public AdjectiveTag(String id, AdjectiveType type) {
        this.id = id;
        this.type = type;
    }

    public void addForm(String gender, String number, String word) {
        String key = gender.toUpperCase() + "_" + number.toLowerCase();
        forms.put(key, word);
    }

    public String getWord(String gender, String number) {
        String key = gender.toUpperCase() + "_" + number.toLowerCase();
        return forms.getOrDefault(key, id);
    }

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