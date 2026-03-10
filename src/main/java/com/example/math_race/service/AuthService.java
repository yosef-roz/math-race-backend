package com.example.math_race.service;

import com.example.math_race.dto.request.LoginRequest;
import com.example.math_race.dto.request.RegisterRequest;
import com.example.math_race.dto.request.VerifyAccountRequest;
import com.example.math_race.dto.response.LoginResponse;
import com.example.math_race.entities.TokenEntity;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import com.example.math_race.repositories.AuthRepository;
import com.example.math_race.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.example.math_race.entities.TokenEntity.TokenType.*;

@Service
@Transactional(readOnly = true)
public class AuthService {

    @Autowired
    private AuthRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;


    @Transactional
    public LoginResponse loginUser(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail());

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new LogicException(ErrorCode.AUTH_FAILED);
        }

        if (!user.isVerified()){
            TokenEntity token = tokenRepository.findTokensByUserAndType(user,VERIFICATION).get(0);
            token.setRevoked(true);
            tokenRepository.save(token);

            // בעיה

            TokenEntity newToken = tokenService.createTokenEntity(user, VERIFICATION);
            emailService.sendVerificationEmail(user,newToken);
            throw new LogicException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }

        TokenEntity tokenEntity = tokenService.createTokenEntity(user, SESSION);

        return new LoginResponse(
                tokenEntity.getToken(),
                user.getUsername(),
                user.getEmail()
        );
    }

    @Transactional
    public void registerUser(RegisterRequest request) {
        UserEntity user = userRepository.findByEmailOrUsername(request.getEmail(),request.getUsername());

        if (user != null) {
            if (user.isDeleted()) {
                throw new LogicException(ErrorCode.REGISTRATION_FAILED);
            }

            if (user.getEmail().equals(request.getEmail())) {
                throw new LogicException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }

            throw new LogicException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        user = new UserEntity(request.getUsername(), request.getPassword(), request.getEmail());
        userRepository.save(user);

        TokenEntity token = tokenService.createTokenEntity(user, VERIFICATION);

        emailService.sendVerificationEmail(user,token);
    }

    @Transactional
    public void verifyAccount(VerifyAccountRequest request) {
        TokenEntity token = tokenRepository.findByToken(request.getToken());

       if (token == null || !token.isValid() || token.isDeleted() || token.getType() != VERIFICATION) {
           throw new LogicException(ErrorCode.INVALID_TOKEN);
       }

       token.setRevoked(true);
       tokenRepository.save(token);

       UserEntity user = token.getUser();
       user.setVerified(true);
       userRepository.save(user);
    }
}
