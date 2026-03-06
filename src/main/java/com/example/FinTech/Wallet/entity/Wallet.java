package com.example.FinTech.Wallet.entity;

import com.example.FinTech.Wallet.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Wallet extends BaseEntity{

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;

    @Version
    private Integer version;
}