package com.dev.funcinema.service;

import com.dev.funcinema.exception.InvalidVerificationException;
import com.dev.funcinema.exception.ResourceNotFoundException;
import com.dev.funcinema.model.User;
import com.dev.funcinema.model.Verification;
import com.dev.funcinema.model.Verification.VerificationType;
import com.dev.funcinema.repository.UserRepository;
import com.dev.funcinema.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository verificationRepository;

    private final UserRepository userRepository;

    private final JavaMailSender mailSender;

    @Value("${otp.expiration}")
    private long otpExpirationMs;

    @Value("${otp.length}")
    private int otpLength;

    private final SecureRandom random = new SecureRandom();

    @Async("virtualThreadTaskExecutor")
    @Transactional
    public void sendEmailVerification(User user) {
        // Delete any existing unverified codes
        verificationRepository.deleteByUserAndType(user, VerificationType.EMAIL);

        // Generate OTP code
        String otpCode = generateOTP();

        // Create verification record
        Verification verification = Verification.builder()
                .user(user)
                .code(otpCode)
                .type(VerificationType.EMAIL)
                .expiresAt(LocalDateTime.now().plusSeconds(otpExpirationMs / 1000))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        verificationRepository.save(verification);

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Email Verification Code");
        message.setText("Your verification code is: " + otpCode +
                "\nThis code will expire in " + (otpExpirationMs / 60000) + " minutes.");

        try {
            mailSender.send(message);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email: {}", e.getMessage());
        }
    }

    @Async("virtualThreadTaskExecutor")
    @Transactional
    public void sendMobileVerification(User user) {
        // Delete any existing unverified codes
        verificationRepository.deleteByUserAndType(user, VerificationType.MOBILE);

        // Generate OTP code
        String otpCode = generateOTP();

        // Create verification record
        Verification verification = Verification.builder()
                .user(user)
                .code(otpCode)
                .type(VerificationType.MOBILE)
                .expiresAt(LocalDateTime.now().plusSeconds(otpExpirationMs / 1000))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        verificationRepository.save(verification);

        // In a real implementation, this would integrate with an SMS service
        // For this example, we'll log the code
        log.info("SMS Verification code for {}: {}", user.getMobileNumber(), otpCode);
        log.info("In a real implementation, this would send an SMS to the user's mobile number");
    }

    @Transactional
    public boolean verifyEmail(String username, String code) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Verification verification = verificationRepository
                .findByUserAndTypeAndCodeAndUsedFalse(user, VerificationType.EMAIL, code)
                .orElseThrow(() -> new InvalidVerificationException("Invalid or expired verification code"));

        if (verification.isExpired()) {
            verificationRepository.delete(verification);
            throw new InvalidVerificationException("Verification code has expired");
        }

        // Mark as used
        verification.setUsed(true);
        verificationRepository.save(verification);

        // Update user
        user.setEmailVerified(true);
        userRepository.save(user);

        return true;
    }

    @Transactional
    public boolean verifyMobile(String username, String code) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Verification verification = verificationRepository
                .findByUserAndTypeAndCodeAndUsedFalse(user, VerificationType.MOBILE, code)
                .orElseThrow(() -> new InvalidVerificationException("Invalid or expired verification code"));

        if (verification.isExpired()) {
            verificationRepository.delete(verification);
            throw new InvalidVerificationException("Verification code has expired");
        }

        // Mark as used
        verification.setUsed(true);
        verificationRepository.save(verification);

        // Update user
        user.setMobileVerified(true);
        userRepository.save(user);

        return true;
    }

    @Async("virtualThreadTaskExecutor")
    @Transactional
    public CompletableFuture<Boolean> verifyEmailAsync(String username, String code) {
        return CompletableFuture.completedFuture(verifyEmail(username, code));
    }

    @Async("virtualThreadTaskExecutor")
    @Transactional
    public CompletableFuture<Boolean> verifyMobileAsync(String username, String code) {
        return CompletableFuture.completedFuture(verifyMobile(username, code));
    }

    @Transactional
    public void resendVerification(String username, VerificationType type) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (type == VerificationType.EMAIL) {
            sendEmailVerification(user);
        } else if (type == VerificationType.MOBILE) {
            sendMobileVerification(user);
        }
    }

    // OTP generation
    private String generateOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
