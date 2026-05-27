package com.example.math_race.questionGenerator.tags.types;

import com.example.math_race.entities.dictionary.ItemEntity;
import com.example.math_race.questionGenerator.tags.core.MatchableTag;
import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.ItemCategory;
import com.example.math_race.questionGenerator.tags.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemTag implements MatchableTag {
    private String id;
    private String singular;
    private String plural;
    private Gender gender;
    private Set<ItemCategory> categories;
    private Set<UnitType> allowedUnits;

    public ItemTag(String id, String singular, String plural, Gender gender, Set<UnitType> allowedUnits, ItemCategory... categories) {
        this.id = id;
        this.singular = singular;
        this.plural = plural;
        this.gender = gender;
        this.allowedUnits = allowedUnits != null ? allowedUnits : new HashSet<>();
        this.categories = new HashSet<>(Arrays.asList(categories));
    }

    public ItemTag(ItemEntity entity) {
        this.id = entity.getItemId();
        this.singular = entity.getSingular();
        this.plural = entity.getPlural();
        this.gender = entity.getGender();
        this.categories = entity.getCategories();
        this.allowedUnits = entity.getAllowedUnits();
    }

    @Override
    public String getProperty(String key) {
        if (key == null || key.isEmpty()) return singular;

        String normalizedKey = key.trim().toLowerCase();

        return switch (normalizedKey) {
            case "p", "plural" -> plural;
            case "g", "gender" -> gender.name();
            case "id" -> id;
            case "t","type","c","categories" -> categories.stream()
                    .map(Enum::name)
                    .collect(java.util.stream.Collectors.joining("|"));
            case "one" -> gender == Gender.MALE ? "אחד" : "אחת";
            case "u","unit","unit_type","allowed_unit" -> {
                if (allowedUnits.isEmpty()) yield "NONE";
                yield allowedUnits.stream()
                        .map(Enum::name)
                        .collect(java.util.stream.Collectors.joining("|"));
            }
            default -> singular;
        };
    }

    @Override
    public boolean matches(Map<String, String> constraints) {
        String reqType = null;
        String reqUnit = null;
        String reqId = null;

        for (Map.Entry<String, String> entry : constraints.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) continue;

            String key = entry.getKey().trim().toLowerCase();
            String value = entry.getValue().trim();

            if (value.equals("?")) continue;

            switch (key) {
                case "t", "type", "c", "categories" -> reqType = value;
                case "u", "unit", "unit_type", "allowed_unit" -> reqUnit = value;
                case "id" -> reqId = value;
            }
        }

        if (reqType != null) {
            if (!matchComplexExpression(reqType, this.categories, ItemCategory.class)) return false;
        }

        if (reqUnit != null) {
            if (!matchComplexExpression(reqUnit, this.allowedUnits, UnitType.class)) return false;
        }

        if (reqId != null) {
            boolean isNegated = reqId.startsWith("!");
            String val = isNegated ? reqId.substring(1).trim() : reqId;
            return isNegated != this.id.equalsIgnoreCase(val);
        }

        return true;
    }

    private <T extends Enum<T>> boolean matchComplexExpression(String rawExpr, Set<T> actualValues, Class<T> enumClass) {
        rawExpr = rawExpr.toUpperCase();
        boolean isNegated = rawExpr.startsWith("!");
        if (isNegated) rawExpr = rawExpr.substring(1).trim();

        String[] orGroups = rawExpr.split("\\|");
        boolean expressionMatch = false;

        for (String group : orGroups) {
            String[] andRequirements = group.split("&");
            boolean allInGroupMatch = true;

            for (String req : andRequirements) {
                try {
                    T enumValue = Enum.valueOf(enumClass, req.trim());
                    if (!actualValues.contains(enumValue)) {
                        allInGroupMatch = false;
                        break;
                    }
                } catch (IllegalArgumentException e) {
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
}
