package com.example.math_race.controller;

import antlr.Token;
import com.example.math_race.entities.TokenEntity;
import com.example.math_race.entities.TokenType;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.repositories.BaseRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Date;

@RestController
public class LoginController {

    @Autowired
    private BaseRepository baseRepository;

    // http://localhost:8085/test-login
    @RequestMapping("/test-login")
    public String testLogin() {
        UserEntity user = new UserEntity("ryan1299", "xxxx", "r@gmail.com");
        TokenEntity token = new TokenEntity("token", TokenType.SESSION, user, new Date(), null, null);
        baseRepository.save(user);
        baseRepository.save(token);
        return "השרת של Math Race עובד! הבקשה הגיעה אליי.";
    }
}
