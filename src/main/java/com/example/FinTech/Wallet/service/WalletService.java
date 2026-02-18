package com.example.FinTech.Wallet.service;

import com.example.FinTech.Wallet.entity.Wallet;
import com.example.FinTech.Wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Transactional
    public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
        if(amount==null || amount.compareTo(BigDecimal.ZERO)<=0) {
            throw new IllegalArgumentException("Invalid transfer amount");
        }

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to same wallet");
        }

        Wallet sender = walletRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Wallet receiver = walletRepository.findById(toId)
                .orElseThrow(()-> new RuntimeException("Receiver not found"));


        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // Debit & Credit
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

    }
}