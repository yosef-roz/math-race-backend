package com.example.math_race.controller;

import com.example.math_race.entities.UserEntity;
import com.example.math_race.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class LoginController {

    @Autowired
    private BaseRepository baseRepository;

    @PostConstruct
    public void init() {
    }

    // http://localhost:8080/test-login
    @RequestMapping("/test-login")
    public String testLogin() {
        baseRepository.save(new UserEntity());
        return "השרת של Math Race עובד! הבקשה הגיעה אליי.";
    }
}
