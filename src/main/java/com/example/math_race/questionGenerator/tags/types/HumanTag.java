package com.example.math_race.questionGenerator.tags.types;

import com.example.math_race.questionGenerator.tags.core.QuestionEntity;
import com.example.math_race.questionGenerator.tags.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanTag implements QuestionEntity {
    private String name;
    private Gender gender;

    @Override
    public String getProperty(String key) {
        if ("n".equals(key)) return name;
        if ("g".equals(key)) return gender.toString();

        if ("he_she".equals(key)) {
            return gender == Gender.MALE ? "הוא" : "היא";
        }
        if ("his_hers".equals(key)) {
            return gender == Gender.MALE ? "שלו" : "שלה";
        }
        if ("to_him_her".equals(key)) {
            return gender == Gender.MALE ? "לו" : "לה";
        }

        if ("one".equals(key)) {
            return gender == Gender.MALE ? "אחד" : "אחת";
        }

        if ("loves".equals(key) || "likes".equals(key)) {
            return gender == Gender.MALE ? "אוהב" : "אוהבת";
        }

        if ("from_him_her".equals(key)) {
            return gender == Gender.MALE ? "ממנו" : "ממנה";
        }

        return name;
    }



    public boolean matches(Map<String, String> constraints) {
        if (constraints.containsKey("g") && !constraints.get("g").equals("?")) {
            String reqGender = constraints.get("g").trim().toLowerCase();

            if (reqGender.startsWith("!")) {
                String excludedGender = reqGender.substring(1);

                if ((excludedGender.equals("m") || excludedGender.equals("male")) && this.gender == Gender.MALE) return false;
                if ((excludedGender.equals("f") || excludedGender.equals("female")) && this.gender == Gender.FEMALE) return false;
            } else {

                if ((reqGender.equals("m") || reqGender.equals("male")) && this.gender != Gender.MALE) return false;
                if ((reqGender.equals("f") || reqGender.equals("female")) && this.gender != Gender.FEMALE) return false;
            }
        }

        if (constraints.containsKey("n") && !constraints.get("n").equals("?")) {
            String reqName = constraints.get("n").trim();

            if (reqName.startsWith("!")) {
                String excludedName = reqName.substring(1);
                if (this.name.equalsIgnoreCase(excludedName)) return false;
            } else {
                if (!this.name.equalsIgnoreCase(reqName)) return false;
            }
        }

        return true;
    }
}
