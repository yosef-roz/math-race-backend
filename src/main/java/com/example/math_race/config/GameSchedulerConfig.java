package com.example.math_race.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class GameSchedulerConfig {

    public static final String GAME_SCHEDULER_BEAN_NAME = "gameTaskScheduler";

    @Bean(name = GAME_SCHEDULER_BEAN_NAME)
    public ThreadPoolTaskScheduler gameTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(20);
        scheduler.setThreadNamePrefix("RaceTimer-");
        scheduler.initialize();

        return scheduler;
    }
}