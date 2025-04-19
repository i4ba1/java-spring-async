package com.dev.funcinema.dto;


import com.dev.funcinema.model.Purchase;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Payment Method DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {

    private String code;
    private String name;
    private String description;
    private boolean enabled;
}
