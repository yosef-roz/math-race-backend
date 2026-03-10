package com.example.math_race.service;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailRequest {
    private String to;
    private String subject;
    private String body;
    private boolean isHtml;

    public MailRequest(String to, String subject, String body, boolean isHtml) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.isHtml = isHtml;
    }

}