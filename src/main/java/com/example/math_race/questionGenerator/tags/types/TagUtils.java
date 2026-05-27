package com.example.math_race.questionGenerator.tags.types;

import java.util.Set;
import java.util.stream.Collectors;

public class TagUtils {

    public static boolean matchComplexStringExpression(String rawExpr, Set<String> actualValues) {
        boolean isNegated = rawExpr.startsWith("!");
        if (isNegated) rawExpr = rawExpr.substring(1).trim();

        String[] orGroups = rawExpr.split("\\|");
        boolean expressionMatch = false;

        for (String group : orGroups) {
            String[] andRequirements = group.split("&");
            boolean allInGroupMatch = true;

            for (String req : andRequirements) {
                if (!actualValues.contains(req.trim())) {
                    allInGroupMatch = false;
                    break;
                }
            }

            if (allInGroupMatch) {
                expressionMatch = true;
                break;
            }
        }

        return isNegated != expressionMatch;
    }

    public static <T extends Enum<T>> boolean matchComplexExpression(String rawExpr, Set<T> actualValues, Class<T> enumClass) {
        Set<String> stringValues = actualValues.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return matchComplexStringExpression(rawExpr.toUpperCase(), stringValues);
    }
}
