package com.example.math_race.service;

import com.example.math_race.email.EmailSender;
import com.example.math_race.email.MailRequest;
import com.example.math_race.entities.TokenEntity;
import com.example.math_race.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Service
@Transactional(readOnly = true)
public class EmailService {

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Async
    public void sendVerificationEmail(UserEntity to, TokenEntity token) {
        Context context = new Context();

        context.setVariable("name", to.getUsername());
        context.setVariable("verificationUrl", "http://localhost:5174/verify/" + token.getToken());

        String htmlContent = templateEngine.process("verify-email", context);

        MailRequest mailRequest = new MailRequest(
                to.getEmail(),
                "אימות רישום - Math Race",
                htmlContent,
                true
        );

        emailSender.sendEmail(mailRequest);
    }

    @Async
    public void sendPasswordResetEmail(UserEntity to, TokenEntity token) {
        Context context = new Context();

        context.setVariable("name", to.getUsername());
        context.setVariable("resetUrl", "http://localhost:5174/reset-password/" + token.getToken());

        String htmlContent = templateEngine.process("reset-password", context);

        MailRequest mailRequest = new MailRequest(
                to.getEmail(),
                "איפוס סיסמה - Math Race",
                htmlContent,
                true
        );

        emailSender.sendEmail(mailRequest);
    }

    @Async
    public void sendPasswordChangedEmail(UserEntity to) {
        Context context = new Context();

        context.setVariable("name", to.getUsername());
        context.setVariable("loginUrl", "http://localhost:5174/login");

        String htmlContent = templateEngine.process("password-changed", context);

        MailRequest mailRequest = new MailRequest(
                to.getEmail(),
                "הסיסמה שלך שונתה - Math Race",
                htmlContent,
                true
        );

        emailSender.sendEmail(mailRequest);
    }
}