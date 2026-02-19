package com.example.FinTech.Wallet.repository;

import com.example.FinTech.Wallet.entity.WalletTransaction;
import jakarta.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<WalletTransaction, Long> {
    Optional<WalletTransaction> findByIdempotencyKey(String idempotencyKey);
}
