package com.dev.funcinema.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Verification Request DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Code is required")
    private String code; // "email" or "mobile"
}