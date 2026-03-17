package com.example.math_race.email;

import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailSender {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(MailRequest mailRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(mailRequest.getTo());
            helper.setSubject(mailRequest.getSubject());
            helper.setText(mailRequest.getBody(), mailRequest.isHtml());

            System.out.println(mailRequest.getBody());

            mailSender.send(message);

        } catch (MessagingException | MailAuthenticationException e) {
            throw new LogicException(ErrorCode.EMAIL_SEND_FAILED);
        } catch (MailSendException e) {
            if (e.getMessage().contains("550")) {
                throw new LogicException(ErrorCode.EMAIL_NOT_FOUND);
            }
            throw new LogicException(ErrorCode.EMAIL_SEND_FAILED);
        } catch (Exception e) {
            throw new LogicException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}