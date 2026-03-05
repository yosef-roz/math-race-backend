package com.example.math_race;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class MathRaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MathRaceApplication.class, args);

		System.out.println("\n----------------------------------------------------------");
		System.out.println("  Application 'Math Race' is running successfully!");
		System.out.println("----------------------------------------------------------\n");
	}
}
