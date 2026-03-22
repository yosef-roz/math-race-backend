package com.example.math_race.race.questions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Human implements QuestionEntity {
    private String name;
    private Gender gender;

    @Override
    public String getProperty(String key) {
        if ("n".equals(key)) return name;
        if ("g".equals(key)) return gender.toString();
        return name;
    }

    public boolean matches(Map<String, String> constraints) {
        if (constraints.containsKey("g") && !constraints.get("g").equals("?")) {
            String reqGender = constraints.get("g").trim().toLowerCase();
            if ((reqGender.equals("m") || reqGender.equals("male")) && this.gender != Gender.MALE) return false;
            if ((reqGender.equals("f") || reqGender.equals("female"))&& this.gender != Gender.FEMALE) return false;
        }

        if (constraints.containsKey("n") && !constraints.get("n").equals("?")) {
            String reqName = constraints.get("n").trim();
            if (!this.name.equalsIgnoreCase(reqName)) return false;
        }

        return true;
    }
}
