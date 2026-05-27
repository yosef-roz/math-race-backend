package com.example.math_race.dto.http.request;

import lombok.Data;

@Data
public class RequestMetadata {
    private String authorization;
    private String guestToken;
    private String userAgent;
    private String ipAddress;
}
