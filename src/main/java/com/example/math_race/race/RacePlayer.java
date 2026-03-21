package com.example.math_race.race;

import com.example.math_race.race.questions.MathQuestion;
import lombok.*;

import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RacePlayer extends RaceAccount{
    private MathQuestion currentQuestion;
    private long questionRemainingTimeMillis;
    private long questionStartTimeMillis;
    private int currentScore;

    public RacePlayer(String accountId,String sessionActive, String joinToken ,String nickname){
        super(accountId,sessionActive,joinToken,nickname);
        this.currentScore = 0;
    }

    public boolean checkAnswer(String answer){
        return currentQuestion != null && Objects.equals(currentQuestion.getCorrectAnswer(),answer);
    }

    public void addScore(int score){
        this.currentScore += score;
    }

    public long getCalculatedQuestionRemainingTime(RaceStatus currentRaceStatus) {
        if (currentRaceStatus != RaceStatus.IN_PROGRESS) {
            return this.questionRemainingTimeMillis;
        }

        long timeElapsed = System.currentTimeMillis() - this.questionStartTimeMillis;
        long actualRemaining = this.questionRemainingTimeMillis - timeElapsed;

        return Math.max(0, actualRemaining);
    }

    @Override
    public String toString() {
        return "RacePlayer{" +
                "currentScore= " + currentScore +
                " id= " + getId()+
                " nickname= " + getNickname() +

                '}';
    }
}
