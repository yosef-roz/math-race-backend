package com.example.math_race.race.questions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NumberEntity implements QuestionEntity {
    private int value;

    @Override
    public String getProperty(String key) {
        if (key.startsWith("mul_")) {
            int factor = Integer.parseInt(key.replace("mul_", ""));
            return String.valueOf(value * factor);
        }
        if (key.startsWith("add_")) {
            int bonus = Integer.parseInt(key.replace("add_", ""));
            return String.valueOf(value + bonus);
        }
        return String.valueOf(value);
    }

    public int getValue() { return value; }
}
