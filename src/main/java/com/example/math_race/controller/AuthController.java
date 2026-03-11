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
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request, RequestMetadata metadata) {
        if (request == null || request.getEmail() == null || request.getEmail().isEmpty()
                || request.getPassword() == null || request.getPassword().isEmpty()) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        LoginResponse response = authService.loginUser(request, metadata);
        return ApiResponse.success(response);
    }

    // http://localhost:8085/api/auth/register
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequest request, RequestMetadata metadata) {
        if (request == null || request.getEmail() == null || request.getEmail().isEmpty()
            || request.getPassword() == null ||request.getPassword().isEmpty()
                || request.getUsername() == null || request.getUsername().isEmpty()) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        authService.registerUser(request, metadata);
        // בדיקה איתך
        return ApiResponse.success(null);
    }

    // http://localhost:8085/api/auth/verify-account
    @PostMapping("/verify-account")
    public ApiResponse<Void> verifyAccount(@RequestBody VerifyAccountRequest request, RequestMetadata metadata) {
        if (request == null || request.getToken() == null || request.getToken().isEmpty()) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        authService.verifyAccount(request);
        // בדיקה איתך
        return ApiResponse.success(null);
    }

    // http://localhost:8085/api/auth/forgot-password
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request, RequestMetadata metadata) {
        if (request == null || request.getEmail() == null || request.getEmail().isEmpty()){
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        authService.userForgotPassword(request, metadata);
        // בדיקה איתך
        return ApiResponse.success(null);
    }

    // http://localhost:8085/api/auth/reset-password
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request, RequestMetadata metadata) {
        if (request == null || request.getToken() == null || request.getToken().isEmpty()
                || request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        authService.userResetPassword(request);
        // בדיקה איתך
        return ApiResponse.success(null);
    }

    // http://localhost:8085/api/auth/change-password
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequest request, RequestMetadata metadata) {
        if (request == null || request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        authService.userChangePassword(request,metadata);
        // בדיקה איתך
        return ApiResponse.success(null);
    }
}