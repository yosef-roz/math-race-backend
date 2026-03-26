package com.example.math_race.questionGenerator.tags.types;

import com.example.math_race.questionGenerator.tags.core.QuestionEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NumberTag implements QuestionEntity {
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

        if (key.startsWith("sub_")) {
            int bonus = Integer.parseInt(key.replace("sub_", ""));
            return String.valueOf(value - bonus);
        }

        if (key.startsWith("div_")) {
            int bonus = Integer.parseInt(key.replace("div_", ""));
            return String.valueOf(value / bonus);
        }

        if (key.startsWith("mod_")) {
            int divisor = Integer.parseInt(key.replace("mod_", ""));
            return String.valueOf(value % divisor);
        }


        return String.valueOf(value);
    }

    public int getValue() { return value; }
}
