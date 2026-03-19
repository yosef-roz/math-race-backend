package com.example.math_race.dto.wsMessage;

import com.example.math_race.race.RacePlayer;
import com.example.math_race.race.questions.MathQuestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewQuestionDTO {

    private String expression;
    private List<String> options;
    private int correctOptionIndex;
    private int timeLimitSeconds;
    private int score;
    private String playerId;

    public NewQuestionDTO(MathQuestion question, RacePlayer player){
        this.expression = question.getExpression();
        this.options = question.getOptions();
        this.correctOptionIndex = question.getCorrectOptionIndex();
        this.timeLimitSeconds = question.getTimeLimitSeconds();
        this.score = question.getScore();
        this.playerId = player.getId();

    }
}
