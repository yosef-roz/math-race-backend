package com.example.math_race.questionGenerator.tags.types;

import com.example.math_race.entities.dictionary.HumanEntity;
import com.example.math_race.questionGenerator.tags.core.MatchableTag;
import com.example.math_race.questionGenerator.tags.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanTag implements MatchableTag {
    private String name;
    private Gender gender;

    public HumanTag(HumanEntity entity) {
        this.name = entity.getName();
        this.gender = entity.getGender();
    }

    @Override
    public String getProperty(String key) {
        if (key == null || key.isEmpty()) return name;

        String normalizedKey = key.trim().toLowerCase();

        return switch (normalizedKey) {
            case "n", "name" -> name;
            case "g", "gender" -> gender.toString();
            case "he_she" -> gender == Gender.MALE ? "הוא" : "היא";
            case "his_hers" -> gender == Gender.MALE ? "שלו" : "שלה";
            case "to_him_her" -> gender == Gender.MALE ? "לו" : "לה";
            case "one" -> gender == Gender.MALE ? "אחד" : "אחת";
            case "loves", "likes" -> gender == Gender.MALE ? "אוהב" : "אוהבת";
            case "from_him_her" -> gender == Gender.MALE ? "ממנו" : "ממנה";
            default -> name;
        };
    }

//    @Override
//    public String getProperty(String key) {
//        if ("n".equals(key)) return name;
//        if ("g".equals(key)) return gender.toString();
//
//        if ("he_she".equals(key)) {
//            return gender == Gender.MALE ? "הוא" : "היא";
//        }
//        if ("his_hers".equals(key)) {
//            return gender == Gender.MALE ? "שלו" : "שלה";
//        }
//        if ("to_him_her".equals(key)) {
//            return gender == Gender.MALE ? "לו" : "לה";
//        }
//
//        if ("one".equals(key)) {
//            return gender == Gender.MALE ? "אחד" : "אחת";
//        }
//
//        if ("loves".equals(key) || "likes".equals(key)) {
//            return gender == Gender.MALE ? "אוהב" : "אוהבת";
//        }
//
//        if ("from_him_her".equals(key)) {
//            return gender == Gender.MALE ? "ממנו" : "ממנה";
//        }
//
//        return name;
//    }

//    public boolean matches(Map<String, String> constraints) {
//        if (constraints.containsKey("g") && !constraints.get("g").equals("?")) {
//            String reqGender = constraints.get("g").trim().toUpperCase();
//
//            if (reqGender.startsWith("!")) {
//                String excludedGender = reqGender.substring(1);
//
//                if ((excludedGender.equals("M") || excludedGender.equals("MALE")) && this.gender == Gender.MALE) return false;
//                if ((excludedGender.equals("M") || excludedGender.equals("FEMALE")) && this.gender == Gender.FEMALE) return false;
//
//            } else {
//
//                if ((reqGender.equals("M") || reqGender.equals("MALE")) && this.gender == Gender.MALE) return false;
//                if ((reqGender.equals("F") || reqGender.equals("FEMALE")) && this.gender == Gender.FEMALE) return false;
//            }
//        }
//
//        if (constraints.containsKey("n") && !constraints.get("n").equals("?")) {
//            String reqName = constraints.get("n").trim();
//
//            if (reqName.startsWith("!")) {
//                String excludedName = reqName.substring(1);
//                if (this.name.equalsIgnoreCase(excludedName)) return false;
//            } else {
//                if (!this.name.equalsIgnoreCase(reqName)) return false;
//            }
//        }
//
//        return true;
//    }

    @Override
    public boolean matches(Map<String, String> constraints) {
        String reqGender = null;
        String reqName = null;

        for (Map.Entry<String, String> entry : constraints.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) continue;

            String key = entry.getKey().trim().toLowerCase();
            if (key.equals("g") || key.equals("gender")) {
                reqGender = entry.getValue();
            } else if (key.equals("n") || key.equals("name")) {
                reqName = entry.getValue();
            }
        }

        if (reqGender != null && !reqGender.trim().equals("?")) {
            reqGender = reqGender.trim().toUpperCase();

            if (reqGender.startsWith("!")) {
                String excludedGender = reqGender.substring(1).trim();

                if ((excludedGender.equals("M") || excludedGender.equals("MALE")) && this.gender == Gender.MALE) return false;
                if ((excludedGender.equals("F") || excludedGender.equals("FEMALE")) && this.gender == Gender.FEMALE) return false;
            } else {
                if ((reqGender.equals("M") || reqGender.equals("MALE")) && this.gender != Gender.MALE) return false;
                if ((reqGender.equals("F") || reqGender.equals("FEMALE")) && this.gender != Gender.FEMALE) return false;
            }
        }

        if (reqName != null && !reqName.trim().equals("?")) {
            reqName = reqName.trim();

            if (reqName.startsWith("!")) {
                String excludedName = reqName.substring(1).trim();

                if (this.name.equalsIgnoreCase(excludedName)) return false;
            } else {
                if (!this.name.equalsIgnoreCase(reqName)) return false;
            }
        }

        return true;
    }
}
