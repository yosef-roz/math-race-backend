package com.example.math_race.service;

import com.example.math_race.entities.TokenEntity;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import com.example.math_race.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static com.example.math_race.entities.TokenEntity.TokenType.*;

@Service
@Transactional(readOnly = true)
public class TokenService {

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public TokenEntity createTokenEntity(UserEntity user, TokenEntity.TokenType tokenType, String ipAddress, String userAgent) {
        String randomToken = createToken();
        Date expiresAt = null;
        Instant now = Instant.now();


        if (tokenType == SESSION) {
            expiresAt = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));

        }else if (tokenType == VERIFICATION || tokenType == PASSWORD_RESET) {
            TokenEntity oldToken = tokenRepository.findLatestActiveToken(user,tokenType);

            if (oldToken != null && Duration.between(oldToken.getCreationDate().toInstant(), now).toMinutes() <= 2) {
                throw new LogicException(ErrorCode.EMAIL_COOLDOWN_ACTIVE);
            }

            tokenRepository.invalidateTokensByUserAndType(user, tokenType);
            if (tokenType == VERIFICATION) {
                expiresAt = Date.from(Instant.now().plus(24, ChronoUnit.HOURS));
            }else {
                expiresAt = Date.from(Instant.now().plus(12, ChronoUnit.HOURS));
            }
        }

        TokenEntity token = new TokenEntity(
                randomToken,
                tokenType,
                user,
                expiresAt,
                ipAddress,
                userAgent
        );

        tokenRepository.save(token);

        return token;
    }

    @Transactional
    public TokenEntity createTokenEntity(UserEntity user, TokenEntity.TokenType tokenType) {
        return createTokenEntity(user, tokenType, null, null);
    }

    @Transactional
    public void updateTokenSessionExpiresDate(TokenEntity tokenEntity) {
        if (tokenEntity.getType() == SESSION && tokenEntity.getExpiresAt() != null) {

            Instant now = Instant.now();
            Instant expiresAt = tokenEntity.getExpiresAt().toInstant();
            long daysRemaining = Duration.between(now, expiresAt).toDays();

            if (daysRemaining <= 29) {

                tokenEntity.setExpiresAt(Date.from(now.plus(30, ChronoUnit.DAYS)));
                tokenRepository.save(tokenEntity);
            }
        }
    }

    private String createToken() {
        return UUID.randomUUID().toString();
    }
}
