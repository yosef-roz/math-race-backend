package com.example.math_race.questionGenerator.tags.types;

import com.example.math_race.questionGenerator.tags.core.TemplateTag;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NumberTag implements TemplateTag {
    private int value;
    private int min;
    private int max;

    public NumberTag(String valStr, int min, int max) {
        this.min = min;
        this.max = max;

        if (valStr == null || valStr.equals("?")) {
            this.value = java.util.concurrent.ThreadLocalRandom.current().nextInt(this.min, this.max + 1);
        } else if (valStr.startsWith("!")) {
            try {
                int forbiddenValue = Integer.parseInt(valStr.substring(1));
                if (this.min == this.max && this.min == forbiddenValue) {
                    this.value = this.min;
                } else {
                    int randomNumber;
                    do {
                        randomNumber = java.util.concurrent.ThreadLocalRandom.current().nextInt(this.min, this.max + 1);
                    } while (randomNumber == forbiddenValue);
                    this.value = randomNumber;
                }
            } catch (NumberFormatException e) {
                this.value = java.util.concurrent.ThreadLocalRandom.current().nextInt(this.min, this.max + 1);
            }
        } else {
            try {
                this.value = Integer.parseInt(valStr);
            } catch (NumberFormatException e) {
                this.value = java.util.concurrent.ThreadLocalRandom.current().nextInt(this.min, this.max + 1);
            }
        }
    }

    @Override
    public String getProperty(String key) {
        if (key == null || key.trim().isEmpty()) {
            return String.valueOf(value);
        }

        key = key.trim().toLowerCase();

        switch (key) {
            case "min" -> {
                return String.valueOf(min);
            }
            case "max" -> {
                return String.valueOf(max);
            }
            case "abs" -> {
                return String.valueOf(Math.abs(value));
            }
        }

        try {
            if (key.startsWith("mul_")) {
                int factor = Integer.parseInt(key.substring(4));
                return String.valueOf(value * factor);
            }
            if (key.startsWith("add_")) {
                int bonus = Integer.parseInt(key.substring(4));
                return String.valueOf(value + bonus);
            }
            if (key.startsWith("sub_")) {
                int bonus = Integer.parseInt(key.substring(4));
                return String.valueOf(value - bonus);
            }
            if (key.startsWith("div_")) {
                int divisor = Integer.parseInt(key.substring(4));
                if (divisor != 0) return String.valueOf(value / divisor);
            }
            if (key.startsWith("mod_")) {
                int divisor = Integer.parseInt(key.substring(4));
                if (divisor != 0) return String.valueOf(value % divisor);
            }
        } catch (NumberFormatException e) {
            System.out.println("\u001B[31m" + "Warning: Invalid math operation format: " + key + "\u001B[0m");
        }

        return String.valueOf(value);
    }
}
