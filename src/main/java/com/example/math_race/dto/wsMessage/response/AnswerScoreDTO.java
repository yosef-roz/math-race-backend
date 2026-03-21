package com.example.math_race.dto.wsMessage.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerScoreDTO {
    private int score;
    private String playerId;
}
