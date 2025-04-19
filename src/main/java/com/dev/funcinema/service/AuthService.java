package com.dev.funcinema.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.funcinema.dto.AuthResponse;
import com.dev.funcinema.dto.LoginRequest;
import com.dev.funcinema.dto.RegisterRequest;
import com.dev.funcinema.exception.ResourceAlreadyExistsException;
import com.dev.funcinema.exception.UnverifiedAccountException;
import com.dev.funcinema.model.Role;
import com.dev.funcinema.model.Role.ERole;
import com.dev.funcinema.model.User;
import com.dev.funcinema.repository.RoleRepository;
import com.dev.funcinema.repository.UserRepository;
import com.dev.funcinema.security.JwtUtils;
import com.dev.funcinema.security.UserDetailsImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final VerificationService verificationService;

    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        // Check if username, email, or mobile number already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Username is already taken");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already in use");
        }

        if (userRepository.existsByMobileNumber(registerRequest.getMobileNumber())) {
            throw new ResourceAlreadyExistsException("Mobile number is already in use");
        }

        // Create new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .mobileNumber(registerRequest.getMobileNumber())
                .fullName(registerRequest.getFullName())
                .emailVerified(false)
                .mobileVerified(false)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Assign default role (ROLE_USER)
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
        roles.add(userRole);
        user.setRoles(roles);

        // Save user
        User savedUser = userRepository.save(user);

        // Send verification emails and SMS
        verificationService.sendEmailVerification(savedUser);
        verificationService.sendMobileVerification(savedUser);

        return savedUser;
    }

    @Async("virtualThreadTaskExecutor")
    @Transactional
    public CompletableFuture<User> registerUserAsync(RegisterRequest registerRequest) {
        return CompletableFuture.completedFuture(registerUser(registerRequest));
    }

    @Transactional
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Check if email and mobile are verified
        if (!userDetails.isEmailVerified() && !userDetails.isMobileVerified()) {
            throw new UnverifiedAccountException("Both email and mobile number need to be verified");
        } else if (!userDetails.isEmailVerified()) {
            throw new UnverifiedAccountException("Email needs to be verified");
        } else if (!userDetails.isMobileVerified()) {
            throw new UnverifiedAccountException("Mobile number needs to be verified");
        }

        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());

        // Update last login
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + userDetails.getUsername()));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getExpirationTime())
                .username(userDetails.getUsername())
                .emailVerified(userDetails.isEmailVerified())
                .mobileVerified(userDetails.isMobileVerified())
                .build();
    }

    @Async("virtualThreadTaskExecutor")
    @Transactional
    public CompletableFuture<AuthResponse> authenticateUserAsync(LoginRequest loginRequest) {
        return CompletableFuture.completedFuture(authenticateUser(loginRequest));
    }

    @Transactional
    public String refreshToken(String refreshToken) {
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
        return jwtUtils.generateJwtToken(SecurityContextHolder.getContext().getAuthentication());
    }

    @Transactional
    public void logout(String refreshToken) {
        // In a stateless JWT-based authentication, we don't need to do anything on the server side
        // The client should discard the tokens
        log.info("User logged out successfully");
    }
}
