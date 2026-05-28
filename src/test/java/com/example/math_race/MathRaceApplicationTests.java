package com.example.math_race;

import com.example.math_race.questionGenerator.QuestionEngine;
import com.example.math_race.questionGenerator.question.QuestionTemplate;
import com.example.math_race.questionGenerator.question.MathQuestion;
import com.example.math_race.service.QuestionTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MathRaceApplicationTests {

	@Autowired
	QuestionTemplateService questionTemplateService;

	@Autowired
	QuestionEngine questionEngine;

    @Test
    void contextLoads() {

//        String tex = "[HUMAN:g=!m:*:#1] [#1:g]";
//        System.out.println(questionEngine.evaluateTemplate(tex,new HashMap<>()));


        List<QuestionTemplate> allTemplates = new ArrayList<>();

        allTemplates.addAll(questionTemplateService.getTemplatesByDifficulty("easy"));
        allTemplates.addAll(questionTemplateService.getTemplatesByDifficulty("medium"));
        allTemplates.addAll(questionTemplateService.getTemplatesByDifficulty("hard"));

        for (QuestionTemplate questionTemplate : allTemplates) {
            MathQuestion mathQuestion = questionEngine.processTemplate(questionTemplate);

            System.out.println(mathQuestion.getId());
            System.out.println("השאלה:");
            System.out.println(mathQuestion.getExpression());
            System.out.println("התשובה : " + mathQuestion.getCorrectAnswer());
            System.out.println("רמז : " + mathQuestion.getHint());
            System.out.println("אופציות:");
            for (String op : mathQuestion.getOptions()){
                System.out.println(op);
            }

            System.out.println("score : " + mathQuestion.getScore());
            System.out.println("seconds : " + mathQuestion.getTimeLimitSeconds());
            System.out.println("===============================================");
        }
    }

}
