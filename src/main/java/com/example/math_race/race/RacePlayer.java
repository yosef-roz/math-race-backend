package com.example.math_race.race;

import com.example.math_race.race.questions.MathQuestion;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RacePlayer extends RaceAccount{
    private MathQuestion currentQuestion;
    private int currentScore;

    public RacePlayer(String accountId,String sessionActive, String joinToken ,String nickname){
        super(accountId,sessionActive,joinToken,nickname);
        this.currentScore = 0;
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
