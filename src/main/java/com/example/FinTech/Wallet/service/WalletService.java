package com.example.FinTech.Wallet.service;

import com.example.FinTech.Wallet.entity.User;
import com.example.FinTech.Wallet.entity.Wallet;
import com.example.FinTech.Wallet.entity.WalletTransaction;
import com.example.FinTech.Wallet.enums.CurrencyType;
import com.example.FinTech.Wallet.enums.TransactionStatus;
import com.example.FinTech.Wallet.exception.InsufficientFundsException;
import com.example.FinTech.Wallet.repository.TransactionRepository;
import com.example.FinTech.Wallet.repository.UserRepository;
import com.example.FinTech.Wallet.repository.WalletRepository;
import jakarta.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;


    @Transactional
    public WalletTransaction transferMoney(Long fromId,
                                           Long toId,
                                           BigDecimal amount,
                                           String idempotencyKey,String loggedInUser) {

        Wallet senderWallet = walletRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (!senderWallet.getUser().getUsername().equals(loggedInUser)) {
            throw new AccessDeniedException("Ownership check failed: This is not your wallet!");
        }

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

        Wallet receiver = walletRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
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


    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Wallet createWalletForUser(String username, BigDecimal initialBalance) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (walletRepository.existsByUser(user)) {
            throw new RuntimeException("User already has a wallet");
        }

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(initialBalance);
        wallet.setCurrencyType(CurrencyType.valueOf("USD"));
//                .orElseThrow(()->new RuntimeException(""))

        return walletRepository.save(wallet);
    }
}