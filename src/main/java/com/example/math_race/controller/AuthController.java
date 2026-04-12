package com.example.math_race.controller;

import com.example.math_race.dto.request.*;
import com.example.math_race.dto.response.ApiResponse;
import com.example.math_race.dto.response.CreateGuestTokenResponse;
import com.example.math_race.dto.response.LoginResponse;
import com.example.math_race.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // http://localhost:8085/api/auth/login
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, RequestMetadata metadata) {
        LoginResponse response = authService.loginUser(request, metadata);
        return ApiResponse.success(response);
    }

    // http://localhost:8085/api/auth/register
    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request, RequestMetadata metadata) {
        authService.registerUser(request, metadata);
        return ApiResponse.success(null);
    }

    // http://localhost:8085/api/auth/verify-account
    @PostMapping("/verify-account")
    public ApiResponse<Void> verifyAccount(@Valid @RequestBody VerifyAccountRequest request, RequestMetadata metadata) {
        authService.verifyAccount(request);
        return ApiResponse.success(null);
    }

    // http://localhost:8085/api/auth/forgot-password
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, RequestMetadata metadata) {
        authService.userForgotPassword(request, metadata);
        return ApiResponse.success(null);
    }

    // http://localhost:8085/api/auth/reset-password
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request, RequestMetadata metadata) {
        authService.userResetPassword(request);
        return ApiResponse.success(null);
    }

    // http://localhost:8085/api/auth/change-password
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request, RequestMetadata metadata) {
        authService.userChangePassword(request,metadata);
        return ApiResponse.success(null);
    }

    @PostMapping("/create-guestToken")
    public ApiResponse<CreateGuestTokenResponse> createGuestId(RequestMetadata metadata) {
        CreateGuestTokenResponse createGuestIdResponse = authService.createGuestToken();
        return ApiResponse.success(createGuestIdResponse);
    }
}