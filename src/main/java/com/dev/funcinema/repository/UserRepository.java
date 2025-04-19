package com.dev.funcinema.repository;

import com.dev.funcinema.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// User Repository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByMobileNumber(String mobileNumber);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);
}
