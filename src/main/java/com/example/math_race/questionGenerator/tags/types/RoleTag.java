package com.example.math_race.questionGenerator.tags.types;

import com.example.math_race.questionGenerator.tags.core.MatchableTag;
import com.example.math_race.questionGenerator.tags.core.TemplateTag;
import com.example.math_race.questionGenerator.tags.enums.RoleType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoleTag implements MatchableTag {
    private String id;
    private String singularMale;
    private String pluralMale;
    private String singularFemale;
    private String pluralFemale;
    private RoleType roleType;
    private Set<String> validPlaceIds;

    public RoleTag(String id, String singularMale, String pluralMale,
                   String singularFemale, String pluralFemale,
                   RoleType roleType, String... allowedPlaceIds) {
        this.id = id;
        this.singularMale = singularMale;
        this.pluralMale = pluralMale;
        this.singularFemale = singularFemale;
        this.pluralFemale = pluralFemale;
        this.roleType = roleType;
        this.validPlaceIds = new HashSet<>(Arrays.asList(allowedPlaceIds));
    }

    @Override
    public String getProperty(String key) {
        if ("id".equals(key)) return id;

        // הוספת roleType לפי המפתח
        if ("rt".equalsIgnoreCase(key) || "role_type".equalsIgnoreCase(key)) {
            return roleType.name();
        }

        // הוספת validPlaceIds מופרדים ב-|
        if ("vp".equalsIgnoreCase(key) || "valid_places".equalsIgnoreCase(key)) {
            return String.join("|", validPlaceIds);
        }

        if ("sm".equals(key) || "m_s".equals(key) || "sMALE".equals(key)) return singularMale;
        if ("pm".equals(key) || "m_p".equals(key) || "pMALE".equals(key)) return pluralMale;
        if ("sf".equals(key) || "f_s".equals(key) || "sFEMALE".equals(key)) return singularFemale;
        if ("pf".equals(key) || "f_p".equals(key) || "pFEMALE".equals(key)) return pluralFemale;

        return singularMale;
    }

    public boolean matches(Map<String, String> constraints) {

        if (constraints.containsKey("id") && !constraints.get("id").equals("?")) {
            String req = constraints.get("id").trim();
            boolean isNot = req.startsWith("!");
            if (isNot) req = req.substring(1);

            String[] allowedIds = req.split("\\|");
            boolean foundMatch = false;
            for (String allowed : allowedIds) {
                if (this.id.equalsIgnoreCase(allowed.trim())) {
                    foundMatch = true;
                    break;
                }
            }

            if (isNot == foundMatch) return false;
        }

        if (constraints.containsKey("role_type") && !constraints.get("role_type").equals("?")) {
            String req = constraints.get("role_type").trim().toUpperCase();
            boolean isNot = req.startsWith("!");
            if (isNot) req = req.substring(1);

            String[] allowedTypes = req.split("\\|");
            boolean foundMatch = false;
            for (String t : allowedTypes) {
                if (this.roleType.name().equals(t.trim())) {
                    foundMatch = true;
                    break;
                }
            }

            if (isNot == foundMatch) return false;
        }

        if (constraints.containsKey("place_id") && !constraints.get("place_id").equals("?")) {
            String rawExpr = constraints.get("place_id").trim();
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
                    if (!this.validPlaceIds.contains(req.trim())) {
                        allInGroupMatch = false;
                        break;
                    }
                }

                if (allInGroupMatch) {
                    expressionResult = true;
                    break;
                }
            }

            if (isNegated == expressionResult) return false;
        }

        return true;
    }
}