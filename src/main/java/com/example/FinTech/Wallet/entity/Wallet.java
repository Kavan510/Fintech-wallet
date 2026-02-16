package com.example.FinTech.Wallet.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private BigDecimal balance;
    private String currency;

    @Version
    private Integer version; // Critical for Fintech apps!
}