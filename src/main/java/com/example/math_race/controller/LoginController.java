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

        RaceEntity race = new RaceEntity();
        race.setName("test");
        race.setRoomCode("565");
        race.setTargetScore(200);
        race.setHost(user);

        RaceParticipantEntity r1 = new RaceParticipantEntity(user);
        RaceParticipantEntity r2 = new RaceParticipantEntity("ryan anderson");

        List<RaceParticipantEntity> users = new ArrayList<>();
        users.add(r1);
        users.add(r2);

        race.setParticipants(users);

        baseRepository.save(race);


        return "השרת של Math Race עובד! הבקשה הגיעה אליי.";
    }
}
