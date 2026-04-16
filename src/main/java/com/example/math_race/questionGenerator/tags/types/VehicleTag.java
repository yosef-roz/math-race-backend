package com.example.math_race.questionGenerator.tags.types;

import com.example.math_race.questionGenerator.tags.core.MatchableTag;
import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleTag implements MatchableTag {
    private String id;
    private String singular;
    private String plural;
    private Gender gender;
    private VehicleType type;

    @Override
    public String getProperty(String key) {
        if ("id".equals(key)) return id;
        if ("s".equals(key)) return singular;
        if ("p".equals(key)) return plural;
        if ("g".equals(key)) return gender.name();
        if ("type".equalsIgnoreCase(key) || "t".equalsIgnoreCase(key)) return type.name();
        return singular;
    }

    @Override
    public boolean matches(Map<String, String> constraints) {
        if (constraints.containsKey("id") && !constraints.get("id").equals("?")) {
            String reqId = constraints.get("id").trim();
            if (reqId.startsWith("!")) {
                if (this.id.equalsIgnoreCase(reqId.substring(1))) return false;
            } else if (!this.id.equalsIgnoreCase(reqId)) {
                return false;
            }
        }

        if (constraints.containsKey("g") && !constraints.get("g").equals("?")) {
            String reqGender = constraints.get("g").trim().toUpperCase();
            boolean isNot = reqGender.startsWith("!");
            String genderVal = isNot ? reqGender.substring(1) : reqGender;

            boolean isMaleMatch = (genderVal.equals("M") || genderVal.equals("MALE")) && this.gender == Gender.MALE;
            boolean isFemaleMatch = (genderVal.equals("F") || genderVal.equals("FEMALE")) && this.gender == Gender.FEMALE;

            if (isNot) {
                if (isMaleMatch || isFemaleMatch) return false;
            } else if (!isMaleMatch && !isFemaleMatch) {
                return false;
            }
        }

        if (constraints.containsKey("type") && !constraints.get("type").equals("?")) {
            String req = constraints.get("type").trim().toUpperCase();
            boolean isNot = req.startsWith("!");
            if (isNot) req = req.substring(1);

            String[] allowedTypes = req.split("\\|");
            boolean foundMatch = false;
            for (String t : allowedTypes) {
                if (this.type.name().equals(t.trim())) {
                    foundMatch = true;
                    break;
                }
            }

            if (isNot == foundMatch) return false;
        }

        return true;
    }
}
