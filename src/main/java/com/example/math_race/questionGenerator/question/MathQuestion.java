package com.example.math_race.questionGenerator.question;

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

    public MathQuestion(){
        this.options = new ArrayList<>();
    }

}
