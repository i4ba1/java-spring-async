package com.dev.funcinema.dto;


import com.dev.funcinema.model.Purchase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Purchase Response DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponse {

    private Long id;
    private Long userId;
    private Long movieId;
    private String movieTitle;
    private BigDecimal amount;
    private Purchase.PaymentMethod paymentMethod;
    private String transactionId;
    private Purchase.PurchaseStatus status;
    private LocalDateTime purchaseDate;
    private LocalDateTime completedDate;
}
