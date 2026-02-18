package com.example.FinTech.Wallet;

import com.example.FinTech.Wallet.entity.Wallet;
import com.example.FinTech.Wallet.enums.CurrencyType;
import com.example.FinTech.Wallet.repository.WalletRepository;
import com.example.FinTech.Wallet.service.WalletService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Rolls back changes after test so your DB stays clean
public class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void testSuccessfulTransfer() {
        // 1. Setup: Create two wallets
        Wallet w1 = new Wallet();
        w1.setBalance(new BigDecimal("100.00"));
        w1.setUserId("user1");
        walletRepository.save(w1);

        Wallet w2 = new Wallet();
        w2.setBalance(new BigDecimal("50.00"));
        w2.setUserId("user2");
        walletRepository.save(w2);

        // 2. Act: Transfer $30
        walletService.transferMoney(w1.getId(), w2.getId(), new BigDecimal("30.00"));

        // 3. Assert: Check final balances
        assertEquals(0, new BigDecimal("70.00").compareTo(walletRepository.findById(w1.getId()).get().getBalance()));
        assertEquals(0, new BigDecimal("80.00").compareTo(walletRepository.findById(w2.getId()).get().getBalance()));
    }

    @Test
    void testInsufficientFundsThrowsException() {
        Wallet w1 = new Wallet();
        w1.setUserId("user1");
        w1.setBalance(new BigDecimal("10.00"));
        w1.setCurrencyType(CurrencyType.valueOf("USD"));
        walletRepository.save(w1);

        Wallet w2 = new Wallet();
        w2.setUserId("user2");
        w2.setBalance(new BigDecimal("50.00"));
        w2.setCurrencyType(CurrencyType.valueOf("USD"));
        walletRepository.save(w2);

        assertThrows(RuntimeException.class, () -> {
            walletService.transferMoney(w1.getId(), w2.getId(), new BigDecimal("100.00"));
        });
    }
}