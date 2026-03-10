package com.example.math_race.service;

import com.example.math_race.entities.TokenEntity;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static com.example.math_race.entities.TokenEntity.TokenType.*;

@Service
@Transactional(readOnly = true)
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    //@Transactional
    public TokenEntity createTokenEntity(UserEntity user, TokenEntity.TokenType tokenType) {
        String randomToken = createToken();
        Date expiresAt = null;

        if (tokenType == SESSION) {
            expiresAt = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));
        }else if (tokenType == VERIFICATION) {
            expiresAt = Date.from(Instant.now().plus(24, ChronoUnit.HOURS));
        }else if (tokenType == PASSWORD_RESET) {
            expiresAt = Date.from(Instant.now().plus(12, ChronoUnit.HOURS));
        }


        TokenEntity token = new TokenEntity(
                randomToken,
                tokenType,
                user,
                expiresAt,
                null,
                null
        );

        tokenRepository.save(token);

        return token;
    }

    private String createToken() {
        return UUID.randomUUID().toString();
    }
}
