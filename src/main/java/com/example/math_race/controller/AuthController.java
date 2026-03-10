package com.example.math_race.controller;

import com.example.math_race.dto.request.*;
import com.example.math_race.dto.response.ApiResponse;
import com.example.math_race.dto.response.LoginResponse;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // http://localhost:8085/api/auth/login
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().isEmpty()
                || request.getPassword() == null || request.getPassword().isEmpty()) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        LoginResponse response = authService.loginUser(request);
        return ApiResponse.success(response);
    }

    // http://localhost:8085/api/auth/register
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().isEmpty()
            || request.getPassword() == null ||request.getPassword().isEmpty()
                || request.getUsername() == null || request.getUsername().isEmpty()) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        authService.registerUser(request);
        // בדיקה איתך
        return ApiResponse.success(null);
    }

    // http://localhost:8085/api/auth/verify-account
    @PostMapping("/verify-account")
    public ApiResponse<Void> verifyAccount(@RequestBody VerifyAccountRequest request) {
        if (request == null || request.getToken() == null || request.getToken().isEmpty()) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        authService.verifyAccount(request);
        // בדיקה איתך
        return ApiResponse.success(null);
    }

    // http://localhost:8085/api/auth/forgot-password
    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody ForgotPasswordRequest request) {
        System.out.println("--- Forgot Password Request Received ---");
        System.out.println("Email to reset: " + request.getEmail());
    }

    // http://localhost:8085/api/auth/change-password
    @PostMapping("/change-password")
    public void changePassword(@RequestBody ChangePasswordRequest request) {
        System.out.println("--- Change Password Request Received ---");
        System.out.println("New Password: " + request.getNewPassword());
    }
}