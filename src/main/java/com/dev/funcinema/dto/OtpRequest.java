package com.dev.funcinema.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// OTP Request DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "OTP type is required")
    private String type; // "email" or "mobile"
}