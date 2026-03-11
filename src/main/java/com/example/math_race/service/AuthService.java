package com.example.math_race.service;

import com.example.math_race.dto.request.*;
import com.example.math_race.dto.response.LoginResponse;
import com.example.math_race.entities.TokenEntity;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import com.example.math_race.repositories.AuthRepository;
import com.example.math_race.repositories.TokenRepository;
import com.example.math_race.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    public LoginResponse loginUser(LoginRequest request, RequestMetadata metadata) {
        UserEntity user = userRepository.findByEmail(request.getEmail());

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new LogicException(ErrorCode.AUTH_FAILED);
        }

        if (!user.isVerified()){
            resendVerificationEmail(user, metadata);
            throw new LogicException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }

        TokenEntity tokenEntity = tokenService.createTokenEntity(user, SESSION,
                metadata.getIpAddress(),  metadata.getUserAgent());

        return new LoginResponse(
                tokenEntity.getToken(),
                365,
                user.getUsername(),
                user.getEmail()
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resendVerificationEmail(UserEntity user, RequestMetadata metadata) {
        TokenEntity newToken = tokenService.createTokenEntity(user, VERIFICATION,
                metadata.getIpAddress(),  metadata.getUserAgent());
        emailService.sendVerificationEmail(user,newToken);
    }

    @Transactional
    public void registerUser(RegisterRequest request, RequestMetadata metadata) {
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

        TokenEntity token = tokenService.createTokenEntity(user, VERIFICATION,
                metadata.getIpAddress(), metadata.getUserAgent());

        emailService.sendVerificationEmail(user,token);
    }

    @Transactional
    public void verifyAccount(VerifyAccountRequest request) {
        TokenEntity token = tokenRepository.findByToken(request.getToken());

       if (token == null || !token.isValid() || token.isDeleted() || token.getType() != VERIFICATION) {
           throw new LogicException(ErrorCode.INVALID_TOKEN);
       }

       UserEntity user = token.getUser();
       if (user.isDeleted()) {
           throw new LogicException(ErrorCode.ACCOUNT_NOT_FOUND);
       }

       token.setRevoked(true);
       tokenRepository.save(token);

       user.setVerified(true);
       userRepository.save(user);
    }

    @Transactional
    public void userForgotPassword(ForgotPasswordRequest request, RequestMetadata metadata) {
        UserEntity user = userRepository.findByEmail(request.getEmail());

        if (user == null || user.isDeleted()) {
            throw new LogicException(ErrorCode.EMAIL_NOT_EXISTS);
        }

        if (!user.isVerified()){
            throw new LogicException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }

        TokenEntity token = tokenService.createTokenEntity(user, PASSWORD_RESET,
                metadata.getIpAddress(),  metadata.getUserAgent());
        emailService.sendPasswordResetEmail(user,token);
    }

    @Transactional
    public void userChangePassword(ChangePasswordRequest request, RequestMetadata metadata) {
        TokenEntity token = tokenRepository.findByToken(metadata.getAuthorization());

        if (token == null || !token.isValid() || token.isDeleted() || token.getType() != SESSION) {
            throw new LogicException(ErrorCode.INVALID_TOKEN);
        }

        UserEntity user = token.getUser();
        if (user.isDeleted()) {
            throw new LogicException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        if (user.getPassword().equals(request.getNewPassword())) {
            throw new LogicException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        tokenService.updateTokenSessionExpiresDate(token);
        emailService.sendPasswordChangedEmail(user);
    }

    @Transactional
    public void userResetPassword(ResetPasswordRequest request) {
        TokenEntity token = tokenRepository.findByToken(request.getToken());

        if (token == null || !token.isValid() || token.isDeleted() || token.getType() != PASSWORD_RESET) {
            throw new LogicException(ErrorCode.INVALID_TOKEN);
        }

        UserEntity user = token.getUser();
        if (user.isDeleted()) {
            throw new LogicException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        token.setRevoked(true);
        tokenRepository.save(token);

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        tokenRepository.invalidateTokensByUserAndType(user, SESSION);
    }
}