package com.example.math_race.race.questions;

import com.example.math_race.race.RacePlayer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MathQuestionGenerator {

    public MathQuestion generateForPlayer(RacePlayer player) {
        String expression = "המלך ביקש מעידן שיקנה לו 3 תפוחים, עידן קנה 3 תפוחים והביא מהבית עוד 2 ונתן הכל למלך. כמה תפוחים סהכ הביא עידן למלך ?";
        List<String> options = List.of("6","3","5","2");
        int correctOptionIndex = 2;
        int timeLimitSeconds = 15;
        int score = 20;

        return new MathQuestion(expression,options,correctOptionIndex,timeLimitSeconds,score);
    }
}
