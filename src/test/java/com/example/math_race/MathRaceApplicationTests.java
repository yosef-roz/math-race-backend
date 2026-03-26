package com.example.math_race;

import com.example.math_race.questionGenerator.QuestionEngine;
import com.example.math_race.questionGenerator.QuestionTemplate;
import com.example.math_race.race.questions.MathQuestion;
import com.example.math_race.service.QuestionTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MathRaceApplicationTests {

	@Autowired
	QuestionTemplateService questionTemplateService;

	@Autowired
	QuestionEngine questionEngine;

	@Test
	void contextLoads() {

		for (QuestionTemplate questionTemplate : questionTemplateService.getTemplatesByDifficulty("easy")){
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
