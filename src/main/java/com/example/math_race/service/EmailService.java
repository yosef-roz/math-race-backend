package com.example.math_race.service;

import com.example.math_race.entities.TokenEntity;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Async
    public void sendVerificationEmail(UserEntity to, TokenEntity token) {
        Context context = new Context();
        context.setVariable("name", to.getUsername());
        context.setVariable("verificationUrl", "http://localhost:5174/auth/verify/" + token.getToken());

        String htmlContent = templateEngine.process("verify-email", context);

        MailRequest mailRequest = new MailRequest(
                to.getEmail(),
                "אימות רישום - Math Race",
                htmlContent,
                true
        );

        this.sendEmail(mailRequest);
    }

    @Async
    public void sendEmail(MailRequest mailRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(mailRequest.getTo());
            helper.setSubject(mailRequest.getSubject());
            helper.setText(mailRequest.getBody(), mailRequest.isHtml());

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new LogicException(ErrorCode.EMAIL_INVALID_FORMAT);

        } catch (MailAuthenticationException e) {
            e.printStackTrace();
            throw new LogicException(ErrorCode.EMAIL_SEND_FAILED);

        } catch (MailSendException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("550") || errorMessage.contains("Invalid Addresses")) {
                throw new LogicException(ErrorCode.EMAIL_NOT_FOUND);
            }

            e.printStackTrace();
            throw new LogicException(ErrorCode.EMAIL_SEND_FAILED);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LogicException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}