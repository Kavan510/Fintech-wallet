package com.example.FinTech.Wallet.service;

import com.example.FinTech.Wallet.entity.Wallet;
import com.example.FinTech.Wallet.entity.WalletTransaction;
import com.example.FinTech.Wallet.enums.TransactionStatus;
import com.example.FinTech.Wallet.exception.InsufficientFundsException;
import com.example.FinTech.Wallet.repository.TransactionRepository;
import com.example.FinTech.Wallet.repository.WalletRepository;
import jakarta.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;



    @Transactional
    public WalletTransaction transferMoney(Long fromId,
                                           Long toId,
                                           BigDecimal amount,
                                           String idempotencyKey) {

        WalletTransaction existing = transactionRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElse(null);

        if (existing != null) {
            return existing;
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid transfer amount");
        }

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to same wallet");
        }

        Wallet sender = walletRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Wallet receiver = walletRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        WalletTransaction txn = new WalletTransaction();
        txn.setFromWalletId(fromId);
        txn.setToWalletId(toId);
        txn.setAmount(amount);
        txn.setIdempotencyKey(idempotencyKey);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(txn);
    }
}