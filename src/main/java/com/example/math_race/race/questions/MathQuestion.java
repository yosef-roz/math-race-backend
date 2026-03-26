package com.example.math_race.race.questions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class MathQuestion {

    private String id;
    private String expression;
    private List<String> options;
    private String hint;
    private String correctAnswer;
    private int timeLimitSeconds;
    private int score;

    public MathQuestion(){
        this.options = new ArrayList<>();
    }

    public long getTimeLimitMillis(){
        return timeLimitSeconds * 1000L;
    }

}