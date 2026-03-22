package com.example.math_race.race.questions;

import lombok.AllArgsConstructor;
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
        String clean = tag.substring(1, tag.length() - 1);

        List<String> parts = smartSplit(clean, ':');

        String type = parts.get(0);
        Map<String, String> constraints = new HashMap<>();
        String property = "";
        String id = "";

        // טיפול במגבלות (Constraints)
        if (parts.size() > 1 && !type.startsWith("#")) {
            // גם כאן נשתמש ב-smartSplit עבור הנקודה פסיק, למקרה שבעתיד יהיה שם משהו מורכב
            List<String> pairs = smartSplit(parts.get(1), ';');
            for (String pair : pairs) {
                List<String> kv = smartSplit(pair, '=');
                if (kv.size() == 2) constraints.put(kv.get(0), kv.get(1));
            }
        }

        // זיהוי ID ו-Property (בין אם זו הגדרה או הפניה)
        if (type.startsWith("#")) {
            // הפניה כמו [#X:(#Y:p)]
            id = type;
            if (parts.size() > 1) property = parts.get(1); // כאן ה-Property יכול להיות "(#Y:p)"
        } else {
            // הגדרה כמו [ITEM:type=food:s:#1]
            if (parts.size() == 4) {
                property = parts.get(2);
                id = parts.get(3);
            } else if (parts.size() == 3) {
                id = parts.get(2);
            }
        }

        return new TagInfo(type, constraints, property, id);
    }


    private static List<String> smartSplit(String s, char delimiter) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0; // מונה את עומק הסוגריים

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
}