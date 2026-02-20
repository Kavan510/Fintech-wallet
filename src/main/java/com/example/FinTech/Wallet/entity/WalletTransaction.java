package com.example.FinTech.Wallet.entity;

import com.example.FinTech.Wallet.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions", indexes = {
        @Index(name = "idx_idempotency_key", columnList = "idempotencyKey"),
        @Index(name = "idx_from_wallet", columnList = "fromWalletId")
})
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromWalletId;
    private Long toWalletId;
    private BigDecimal amount;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status; // SUCCESS, FAILED, PENDING

    private String description;
    private LocalDateTime timestamp;
}