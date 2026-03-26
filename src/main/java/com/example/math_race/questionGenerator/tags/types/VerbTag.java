package com.example.math_race.questionGenerator.tags.types;

import java.util.HashMap;
import java.util.Map;

public class VerbTag {
    private String id;
    private Map<String, String> forms = new HashMap<>();

    public VerbTag(String id) {
        this.id = id;
    }

    public void addForm(String tense, String gender, String number, String word) {
        String key = tense.toLowerCase() + "_" + gender.toUpperCase() + "_" + number.toLowerCase();
        forms.put(key, word);
    }

    public String getWord(String tense, String gender, String number) {
        String key = tense.toLowerCase() + "_" + gender.toUpperCase() + "_" + number.toLowerCase();
        return forms.getOrDefault(key, id);
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

        return true;
    }
}