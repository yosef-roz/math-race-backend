package com.example.math_race.controller;

import com.example.math_race.entities.RaceEntity;
import com.example.math_race.entities.RaceParticipantEntity;
import com.example.math_race.entities.TokenEntity;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.math_race.entities.TokenEntity.TokenType.*;

@RestController
public class LoginController {

    @Autowired
    private BaseRepository baseRepository;

    // http://localhost:8085/test-login
    @RequestMapping("/test-login")
    public String testLogin() {
        UserEntity user = new UserEntity(Math.random()+"", "xxxx", Math.random()+"");
        TokenEntity token = new TokenEntity(Math.random()+"", SESSION, user, new Date(), null, null);
        baseRepository.save(user);
        baseRepository.save(token);

        RaceEntity race = new RaceEntity("test " + Math.random(), user, 100);

        RaceParticipantEntity r1 = new RaceParticipantEntity(user);
        RaceParticipantEntity r2 = new RaceParticipantEntity("ryan anderson");

        List<RaceParticipantEntity> users = new ArrayList<>();
        users.add(r1);
        users.add(r2);

        System.out.println("הגיע ?");

        for(int i = 0; i < 100; i++) {
            users.add(new RaceParticipantEntity(UUID.randomUUID().toString().substring(0, 6).toUpperCase()));
        }

        race.setParticipants(users);

        System.out.println("שניה לפני");

        baseRepository.saveAll(users);

        System.out.println("אני התחלתי ");

        return "השרת של Math Race עובד! הבקשה הגיעה אליי.";
    }
}
