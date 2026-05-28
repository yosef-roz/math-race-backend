package com.example.math_race.questionGenerator.tags.core;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class TagInfo {
    private String type;
    private Map<String, String> constraints;
    private String property;
    private String id;

    public TagInfo(String type, Map<String, String> constraints, String property, String id) {
        this.type = type;
        this.constraints = constraints;
        this.property = property;
        this.id = id;
    }

    public static TagInfo parse(String tag) {
        String clean = tag.trim().substring(1, tag.length() - 1);
        List<String> parts = smartSplit(clean, ':');

        String type = parts.get(0).trim();
        Map<String, String> constraints = new HashMap<>();
        String property = "";
        String id = "";

        if (type.startsWith("#")) {
            id = type;
            if (parts.size() > 1) {
                property = parts.get(1).trim();
            }
            return new TagInfo(type, constraints, property, id);
        }

        int lastIndex = parts.size() - 1;
        if (lastIndex > 0 && parts.get(lastIndex).trim().startsWith("#")) {
            id = parts.get(lastIndex).trim();
            parts.remove(lastIndex);
        }

        if (parts.size() == 2) {
            String middle = parts.get(1).trim();

            if (hasExposedChar(middle, '=')) {
                parseConstraints(middle, constraints);
            } else {
                property = middle;
            }
        } else if (parts.size() == 3) {

            parseConstraints(parts.get(1).trim(), constraints);
            property = parts.get(2).trim();
        }

        return new TagInfo(type, constraints, property, id);
    }

    private static void parseConstraints(String constraintsPart, Map<String, String> constraints) {
        List<String> pairs = smartSplit(constraintsPart, ';');
        for (String pair : pairs) {
            List<String> kv = smartSplit(pair, '=');
            if (kv.size() >= 2) {
                String key = kv.get(0).trim();

                String value = pair.substring(key.length() + 1).trim();
                constraints.put(key, value);
            }
        }
    }

    public static List<String> smartSplit(String s, char delimiter) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;

        for (char c : s.toCharArray()) {
            if (c == '(') depth++;
            if (c == ')') depth--;


            if (c == delimiter && depth == 0) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result;
    }

    private static boolean hasExposedChar(String s, char target) {
        int depth = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (c == target && depth == 0) {
                return true;
            }
        }
        return false;
    }
}
