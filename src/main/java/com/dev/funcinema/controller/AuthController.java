package com.dev.funcinema.controller;

import java.util.concurrent.CompletableFuture;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.funcinema.dto.AuthResponse;
import com.dev.funcinema.dto.LoginRequest;
import com.dev.funcinema.dto.LogoutRequest;
import com.dev.funcinema.dto.OtpRequest;
import com.dev.funcinema.dto.RefreshTokenRequest;
import com.dev.funcinema.dto.RegisterRequest;
import com.dev.funcinema.dto.VerificationRequest;
import com.dev.funcinema.model.User;
import com.dev.funcinema.model.Verification.VerificationType;
import com.dev.funcinema.service.AuthService;
import com.dev.funcinema.service.VerificationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private VerificationService verificationService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = authService.registerUser(registerRequest);
        return new ResponseEntity<>("User registered successfully. Please check your email and phone for verification codes.",
                HttpStatus.CREATED);
    }

    @PostMapping("/register/async")
    public CompletableFuture<ResponseEntity<String>> registerUserAsync(
            @Valid @RequestBody RegisterRequest registerRequest) {
        return authService.registerUserAsync(registerRequest)
                .thenApply(user -> new ResponseEntity<>(
                        "User registered successfully. Please check your email and phone for verification codes.",
                        HttpStatus.CREATED));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login/async")
    public CompletableFuture<ResponseEntity<AuthResponse>> authenticateUserAsync(
            @Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUserAsync(loginRequest)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/verify/email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody VerificationRequest request) {
        verificationService.verifyEmail(request.getUsername(), request.getCode());
        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/verify/mobile")
    public ResponseEntity<String> verifyMobile(@Valid @RequestBody VerificationRequest request) {
        verificationService.verifyMobile(request.getUsername(), request.getCode());
        return ResponseEntity.ok("Mobile number verified successfully");
    }

    @PostMapping("/verify/email/async")
    public CompletableFuture<ResponseEntity<String>> verifyEmailAsync(
            @Valid @RequestBody VerificationRequest request) {
        return verificationService.verifyEmailAsync(request.getUsername(), request.getCode())
                .thenApply(result -> ResponseEntity.ok("Email verified successfully"));
    }

    @PostMapping("/verify/mobile/async")
    public CompletableFuture<ResponseEntity<String>> verifyMobileAsync(
            @Valid @RequestBody VerificationRequest request) {
        return verificationService.verifyMobileAsync(request.getUsername(), request.getCode())
                .thenApply(result -> ResponseEntity.ok("Mobile number verified successfully"));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@Valid @RequestBody OtpRequest request) {
        VerificationType type = "email".equalsIgnoreCase(request.getType())
                ? VerificationType.EMAIL
                : VerificationType.MOBILE;

        verificationService.resendVerification(request.getUsername(), type);

        String message = type == VerificationType.EMAIL
                ? "Verification email sent successfully"
                : "Verification SMS sent successfully";

        return ResponseEntity.ok(message);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String token = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }
}
