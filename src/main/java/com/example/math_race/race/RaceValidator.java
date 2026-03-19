package com.example.math_race.race;

import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import org.springframework.stereotype.Component;

@Component
public class RaceValidator {
    public static final int MAX_SCORES = 1500;
    public static final int MIN_SCORES = 400;
    public static final int MIN_NAME_LENGTH = 3;

    public void validate(RaceSettings settings) {

        if (settings.getTargetScore() > MAX_SCORES || settings.getTargetScore() < MIN_SCORES) {
            throw new LogicException(ErrorCode.INVALID_RACE_SCORE);
        }

        if (settings.getRaceName() == null || settings.getRaceName().isEmpty()) {
            settings.setDefaultName();
        }else if (settings.getRaceName().length() < MIN_NAME_LENGTH) {
            throw new LogicException(ErrorCode.RACE_NAME_TOO_SHORT);
        }

    }
}