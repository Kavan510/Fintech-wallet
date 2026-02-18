package com.example.FinTech.Wallet.entity;

import com.example.FinTech.Wallet.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromWalletId;
    private Long toWalletId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status; // SUCCESS, FAILED, PENDING

    private LocalDateTime timestamp;
}