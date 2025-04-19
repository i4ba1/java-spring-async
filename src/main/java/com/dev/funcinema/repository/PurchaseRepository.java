package com.dev.funcinema.repository;

import com.dev.funcinema.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

// Purchase Repository
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Set<Purchase> findByUserId(Long userId);

    Set<Purchase> findByMovieId(Long movieId);

    Set<Purchase> findByUserIdAndMovieId(Long userId, Long movieId);
}
