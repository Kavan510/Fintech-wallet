package com.example.FinTech.Wallet.controllers;


import com.example.FinTech.Wallet.entity.WalletTransaction;
import com.example.FinTech.Wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/transfer")
    public ResponseEntity<WalletTransaction> transfer(
            @RequestParam Long from,
            @RequestParam Long to,
            @RequestParam BigDecimal amount,
            @RequestHeader("X-Idempotency-Key") String key) {

        return ResponseEntity.ok(walletService.transferMoney(from, to, amount, key));
    }
}