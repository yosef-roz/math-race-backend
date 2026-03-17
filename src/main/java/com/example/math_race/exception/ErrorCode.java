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

    // Race & Game Errors (1400 Series)
    INVALID_RACE_SCORE(1400, "Winning points must be between 1 and 1000!"),
    RACE_NAME_TOO_SHORT(1401, "Race name must be at least 3 characters long!"),
    RACE_MAX_PLAYERS_EXCEEDED(1402, "Number of players exceeds the allowed limit."),
    USER_ALREADY_IN_RACE(1403, "You are already a host or a participant in another active race!"),
    RACE_NOT_FOUND(1404, "The requested race room does not exist!"),
    RACE_ALREADY_STARTED(1405, "This race has already started and cannot be joined!"),
    RACE_ALREADY_FINISHED(1406, "This race has already ended and is no longer available!"),
    USER_NOT_IN_RACE(1407, "You are not a participant in this race! Please join first."),
    NOT_REGISTERED_FOR_RACE(1408, "You are not registered for this race. Please join the race before performing any actions."),
    DUPLICATE_RACE_CONNECTION(1409, "Action denied. You are already connected to this race from another session or tab."),
    SESSION_TRANSFERRED_TO_NEW_DEVICE(1410, "Your connection has been transferred to a new device or tab. This session is no longer active."),

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
