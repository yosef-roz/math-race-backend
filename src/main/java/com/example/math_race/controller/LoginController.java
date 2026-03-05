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

        for(int i = 0; i < 100; i++) {
            users.add(new RaceParticipantEntity(UUID.randomUUID().toString().substring(0, 6).toUpperCase()));
        }

        race.setPlayers(users);

        System.out.println("שומר מרוץ");
        baseRepository.save(race);
        System.out.println("סיים לשמור מרוץ ");

        System.out.println("טוען מרוץ!");
        RaceEntity n = baseRepository.loadObject(RaceEntity.class, race.getId());
        System.out.println(n.toString());
        System.out.println("משתתפים:");
        System.out.println(n);
        System.out.println("סיים לטעון מרוץ!");

        System.out.println("טוען שחקן");
        RaceParticipantEntity n1 = baseRepository.loadObject(RaceParticipantEntity.class, 1);
        System.out.println(n1.getRace().toString());
        System.out.println("סיים לטעון שחקן");
        return "השרת של Math Race עובד! הבקשה הגיעה אליי.";
    }
}
