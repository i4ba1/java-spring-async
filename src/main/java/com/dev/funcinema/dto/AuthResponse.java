package com.dev.funcinema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// Authentication Response DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private String username;
    private boolean emailVerified;
    private boolean mobileVerified;
}