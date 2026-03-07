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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public WalletTransaction actualTransaction(
            Long fromId,
            Long toId,
            BigDecimal amount,
            String idempotencyKey,
            String loggedInUser){
        Wallet senderWallet = walletRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        User loggedIn = userRepository.findByUsername(loggedInUser)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!senderWallet.getUserId().equals(loggedIn.getId())) {
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


        try {
            walletRepository.save(senderWallet);
            walletRepository.save(receiver);

        } catch (OptimisticLockingFailureException e) {

            throw new OptimisticLockingFailureException(
                    "Concurrent transaction detected. Please retry."
            );
        }


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
    public WalletTransaction transferMoneyWithRetry(Long fromId,
                                                    Long toId,
                                                    BigDecimal amount,
                                                    String idempotencyKey, String loggedInUser) {

        int maxRetry = 3;

        for (int attemp = 0; attemp < maxRetry; attemp++) {
            try {
                return actualTransaction(fromId, toId, amount, idempotencyKey, loggedInUser);
            } catch (OptimisticLockingFailureException e) {

                if (attemp == maxRetry-1) {
                    throw new RuntimeException("Transaction Failed after multiple retries");
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException er) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted during retry", er);
                }
            }

        }
            throw new RuntimeException("Transfer Failed");
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Wallet createWalletForUser(String username, BigDecimal initialBalance) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (walletRepository.existsByUserId(user.getId())) {
            throw new RuntimeException("User already has a wallet");
        }

        Wallet wallet = new Wallet();
        wallet.setUserId(user.getId());
        wallet.setBalance(initialBalance);
        wallet.setCurrencyType(CurrencyType.valueOf("USD"));
        return walletRepository.save(wallet);
    }
}