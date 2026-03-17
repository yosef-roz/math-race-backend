package com.example.math_race.dto.request;

import lombok.Data;

@Data
public class RequestMetadata {
    private String authorization;
    private String guestId;
    private String userAgent;
    private String ipAddress;
}
