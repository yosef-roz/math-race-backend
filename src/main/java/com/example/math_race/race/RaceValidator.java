package com.example.math_race.race;

import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import org.springframework.stereotype.Component;

@Component
public class RaceValidator {
    public static final int MAX_SCORES = 1000;
    public static final int MIN_NAME_LENGTH = 3;

    public void validate(RaceSettings settings) {
        // בדיקת ניקוד
        if (settings.getTargetScore() > MAX_SCORES || settings.getTargetScore() <= 0) {
            throw new LogicException(ErrorCode.INVALID_RACE_SCORE);
        }

        // בדיקת אורך שם
        if (settings.getRaceName() == null || settings.getRaceName().isEmpty()) {
            settings.setDefaultName();
        }else if (settings.getRaceName().length() < MIN_NAME_LENGTH) {
            throw new LogicException(ErrorCode.RACE_NAME_TOO_SHORT);
        }

    }
}