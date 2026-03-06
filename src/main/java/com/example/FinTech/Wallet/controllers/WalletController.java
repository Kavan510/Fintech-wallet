package com.example.FinTech.Wallet.controllers;


import com.example.FinTech.Wallet.entity.Wallet;
import com.example.FinTech.Wallet.entity.WalletTransaction;
import com.example.FinTech.Wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Wallet> createWallet(@RequestParam String username, @RequestParam BigDecimal balance) {
        return ResponseEntity.ok(walletService.createWalletForUser(username, balance));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WalletTransaction> transfer(
            @RequestParam Long fromId,
            @RequestParam Long toId,
            @RequestParam BigDecimal amount,
            @RequestHeader("X-Idempotency-Key") String key,
            Authentication authentication) {

        String loggedInUsername = authentication.getName();

        return ResponseEntity.ok(walletService.transferMoneyWithRetry(fromId, toId, amount, key,loggedInUsername));
    }
}