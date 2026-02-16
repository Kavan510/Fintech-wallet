package com.example.FinTech.Wallet.service;

import com.example.FinTech.Wallet.entity.Wallet;
import com.example.FinTech.Wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Transactional
    public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
        Wallet sender = walletRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        Wallet receiver = walletRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Debit & Credit
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        walletRepository.save(sender);
        walletRepository.save(receiver);
    }
}