package com.dev.funcinema.dto;


import com.dev.funcinema.model.Purchase;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Purchase Request DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Payment method is required")
    private Purchase.PaymentMethod paymentMethod;

    // Payment details - in a real system, these would be handled securely
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
    private String cardHolderName;
}
