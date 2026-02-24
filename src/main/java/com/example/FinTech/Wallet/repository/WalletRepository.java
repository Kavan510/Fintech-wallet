package com.example.FinTech.Wallet.repository;


import com.example.FinTech.Wallet.entity.User;
import com.example.FinTech.Wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(String userId);
    // Add this to your WalletRepository.java
    boolean existsByUser(User user);
}