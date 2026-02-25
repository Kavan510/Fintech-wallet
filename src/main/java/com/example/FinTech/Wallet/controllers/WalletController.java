package com.example.FinTech.Wallet.controllers;


import com.example.FinTech.Wallet.entity.Wallet;
import com.example.FinTech.Wallet.entity.WalletTransaction;
import com.example.FinTech.Wallet.repository.WalletRepository;
import com.example.FinTech.Wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final WalletRepository walletRepository;

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
        Wallet senderWallet = walletRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (!senderWallet.getUser().getUsername().equals(loggedInUsername)) {
            throw new AccessDeniedException("Ownership check failed: This is not your wallet!");
        }

        return ResponseEntity.ok(walletService.transferMoney(fromId, toId, amount, key));
    }
}