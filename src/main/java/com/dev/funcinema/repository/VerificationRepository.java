package com.dev.funcinema.repository;

import com.dev.funcinema.model.User;
import com.dev.funcinema.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Verification Repository
@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    Optional<Verification> findByUserAndTypeAndUsedFalse(User user, Verification.VerificationType type);

    Optional<Verification> findByUserAndTypeAndCodeAndUsedFalse(User user, Verification.VerificationType type, String code);

    void deleteByUserAndType(User user, Verification.VerificationType type);
}
