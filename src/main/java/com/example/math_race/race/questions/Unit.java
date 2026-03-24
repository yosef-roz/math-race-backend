package com.example.math_race.race.questions;

import java.util.Map;

public class Unit implements QuestionEntity {
    private String id;
    private String singular;
    private String plural;
    private Gender gender;
    private UnitType type;

    public Unit(String id, String singular, String plural, Gender gender, UnitType type) {
        this.id = id;
        this.singular = singular;
        this.plural = plural;
        this.gender = gender;
        this.type = type;
    }

    @Override
    public String getProperty(String key) {
        if ("s".equals(key)) return singular;
        if ("p".equals(key)) return plural;
        if ("g".equals(key)) return gender.name();

        if ("t".equalsIgnoreCase(key)) {
            return type.name();
        }

        if ("id".equalsIgnoreCase(key)) {
            return id;
        }

        return singular;
    }

    public boolean matches(Map<String, String> constraints) {
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
                        UnitType requestedType = UnitType.valueOf(req.trim());
                        if (this.type != requestedType) {
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

            if (isNegated == expressionResult) {
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