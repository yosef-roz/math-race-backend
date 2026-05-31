package com.example.math_race;

import com.example.math_race.questionGenerator.QuestionEngine;
import com.example.math_race.questionGenerator.question.QuestionTemplate;
import com.example.math_race.questionGenerator.question.MathQuestion;
import com.example.math_race.questionGenerator.tags.core.TemplateTag;
import com.example.math_race.service.QuestionTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class MathRaceApplicationTests {

	@Autowired
	QuestionTemplateService questionTemplateService;

	@Autowired
	QuestionEngine questionEngine;

    @Test
    void contextLoads() {
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

            System.out.println("===============================================");
        }
    }

    @Test
    void runningTemplate() {

        String template = "[NUM:#A] [NUM:value=(#A:sub_(#A:v))]";


        Map<String, TemplateTag> memoryTags = new HashMap<>();
        String re = questionEngine.evaluateTemplate(template, memoryTags);

        System.out.println(re);
    }

}
