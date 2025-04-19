package com.dev.funcinema.service;

import com.dev.funcinema.dto.PaymentMethodDTO;
import com.dev.funcinema.dto.PurchaseRequest;
import com.dev.funcinema.dto.PurchaseResponse;
import com.dev.funcinema.exception.PaymentProcessingException;
import com.dev.funcinema.exception.ResourceNotFoundException;
import com.dev.funcinema.model.Movie;
import com.dev.funcinema.model.Purchase;
import com.dev.funcinema.model.Purchase.PurchaseStatus;
import com.dev.funcinema.model.User;
import com.dev.funcinema.repository.MovieRepository;
import com.dev.funcinema.repository.PurchaseRepository;
import com.dev.funcinema.repository.UserRepository;
import com.dev.funcinema.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    private final UserRepository userRepository;

    private final MovieRepository movieRepository;

    @Transactional
    public PurchaseResponse purchaseMovie(PurchaseRequest request) {
        // Get current user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify user has verified email and mobile
        if (!user.isEmailVerified() || !user.isMobileVerified()) {
            throw new PaymentProcessingException("Both email and mobile number must be verified to make purchases");
        }

        // Get movie
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + request.getMovieId()));

        // Check if already purchased
        Set<Purchase> existingPurchases = purchaseRepository.findByUserIdAndMovieId(user.getId(), movie.getId());
        if (!existingPurchases.isEmpty()) {
            // Could return existing purchase instead of error
            for (Purchase existingPurchase : existingPurchases) {
                if (existingPurchase.getStatus() == PurchaseStatus.COMPLETED) {
                    throw new PaymentProcessingException("You have already purchased this movie");
                }
            }
        }

        // Process payment (mock implementation)
        String transactionId = processPayment(request);

        // Create purchase record
        Purchase purchase = Purchase.builder()
                .user(user)
                .movie(movie)
                .amount(new BigDecimal("9.99")) // In a real implementation, this would come from the movie pricing
                .paymentMethod(request.getPaymentMethod())
                .transactionId(transactionId)
                .status(PurchaseStatus.COMPLETED)
                .purchaseDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now())
                .build();

        Purchase savedPurchase = purchaseRepository.save(purchase);

        return convertToResponse(savedPurchase);
    }

    @Async("virtualThreadTaskExecutor")
    @Transactional
    public CompletableFuture<PurchaseResponse> purchaseMovieAsync(PurchaseRequest request) {
        return CompletableFuture.completedFuture(purchaseMovie(request));
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponse> getUserPurchases() {
        // Get current user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Set<Purchase> purchases = purchaseRepository.findByUserId(userDetails.getId());

        return purchases.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> getAvailablePaymentMethods() {
        // In a real implementation, this might come from a database or payment gateway API
        return List.of(
                PaymentMethodDTO.builder()
                        .code("CREDIT_CARD")
                        .name("Credit Card")
                        .description("Pay with Visa, Mastercard, or American Express")
                        .enabled(true)
                        .build(),
                PaymentMethodDTO.builder()
                        .code("DEBIT_CARD")
                        .name("Debit Card")
                        .description("Pay with your bank debit card")
                        .enabled(true)
                        .build(),
                PaymentMethodDTO.builder()
                        .code("PAYPAL")
                        .name("PayPal")
                        .description("Pay with your PayPal account")
                        .enabled(true)
                        .build()
        );
    }

    // Mock payment processing
    private String processPayment(PurchaseRequest request) {
        log.info("Processing payment with method: {}", request.getPaymentMethod());

        // In a real implementation, this would integrate with a payment gateway
        // For this example, we'll simulate a payment processor

        // Generate a transaction ID
        String transactionId = UUID.randomUUID().toString();
        log.info("Payment processed successfully. Transaction ID: {}", transactionId);

        return transactionId;
    }

    private PurchaseResponse convertToResponse(Purchase purchase) {
        return PurchaseResponse.builder()
                .id(purchase.getId())
                .userId(purchase.getUser().getId())
                .movieId(purchase.getMovie().getId())
                .movieTitle(purchase.getMovie().getTitle())
                .amount(purchase.getAmount())
                .paymentMethod(purchase.getPaymentMethod())
                .transactionId(purchase.getTransactionId())
                .status(purchase.getStatus())
                .purchaseDate(purchase.getPurchaseDate())
                .completedDate(purchase.getCompletedDate())
                .build();
    }
}
