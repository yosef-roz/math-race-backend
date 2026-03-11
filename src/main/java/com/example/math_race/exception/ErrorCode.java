package com.example.math_race.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // General Errors
    INVALID_INPUT(1000, "Invalid input!"),
    AUTH_FAILED(1001, "Invalid email or password"),
    EMAIL_ALREADY_EXISTS(1002, "Email already exists!"),
    USERNAME_ALREADY_EXISTS(1003, "Username already exists!"),
    REGISTRATION_FAILED(1004, "Registration failed. Please check your details and try again"),
    ACCOUNT_NOT_VERIFIED(1005, "Account not verified. Please check your email for the verification link."),
    EMAIL_NOT_EXISTS(1006, "Email not exists!"),
    ACCOUNT_NOT_FOUND(1007, "Account not found!"),
    PASSWORD_SAME_AS_OLD(1008, "New password cannot be the same as the current password!"),

    // Token & Security Errors (1100 Series)
    INVALID_TOKEN(1100, "The verification token is invalid or has expired."),

    // Email Specific Errors (1200 Series)
    EMAIL_SEND_FAILED(1200, "Failed to send email. Please try again later."),
    EMAIL_NOT_FOUND(1201, "The recipient email address does not exist."),
    EMAIL_INVALID_FORMAT(1202, "Invalid email format!"),

    // Rate Limiting & Flow Errors (1300 Series)
    TOO_MANY_REQUESTS(1300, "Too many requests. Please wait a moment and try again."),
    EMAIL_COOLDOWN_ACTIVE(1301, "Please wait 2 minutes between email requests."),

    // System Errors
    INTERNAL_ERROR(9000, "An unexpected error occurred"),
    NOT_FOUND(9001, "This page is not found.");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
