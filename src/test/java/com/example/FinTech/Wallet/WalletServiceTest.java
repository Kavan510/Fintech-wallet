package com.example.FinTech.Wallet;

import com.example.FinTech.Wallet.entity.User;
import com.example.FinTech.Wallet.entity.Wallet;
import com.example.FinTech.Wallet.enums.CurrencyType;
import com.example.FinTech.Wallet.enums.Role;
import com.example.FinTech.Wallet.exception.InsufficientFundsException;
import com.example.FinTech.Wallet.repository.UserRepository;
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
@Transactional
public class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSuccessfulTransfer() {

        // 🔹 Create Users first
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("pass");
        user1.setRole(Role.ROLE_USER);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("pass");
        user2.setRole(Role.ROLE_USER);
        userRepository.save(user2);

        // 🔹 Create Wallets linked to users
        Wallet w1 = new Wallet();
        w1.setUser(user1);
        w1.setBalance(new BigDecimal("100.00"));
        w1.setCurrencyType(CurrencyType.USD);
        walletRepository.save(w1);

        Wallet w2 = new Wallet();
        w2.setUser(user2);
        w2.setBalance(new BigDecimal("50.00"));
        w2.setCurrencyType(CurrencyType.USD);
        walletRepository.save(w2);

        // 🔹 Act
        walletService.transferMoney(
                w1.getId(),
                w2.getId(),
                new BigDecimal("30.00"),
                "test-key-1"
        );

        // 🔹 Assert
        assertEquals(
                0,
                new BigDecimal("70.00")
                        .compareTo(walletRepository.findById(w1.getId()).get().getBalance())
        );

        assertEquals(
                0,
                new BigDecimal("80.00")
                        .compareTo(walletRepository.findById(w2.getId()).get().getBalance())
        );
    }

    @Test
    void testInsufficientFundsThrowsException() {

        User user1 = new User();
        user1.setUsername("user3");
        user1.setPassword("pass");
        user1.setRole(Role.ROLE_USER);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user4");
        user2.setPassword("pass");
        user2.setRole(Role.ROLE_USER);
        userRepository.save(user2);

        Wallet w1 = new Wallet();
        w1.setUser(user1);
        w1.setBalance(new BigDecimal("10.00"));
        w1.setCurrencyType(CurrencyType.USD);
        walletRepository.save(w1);

        Wallet w2 = new Wallet();
        w2.setUser(user2);
        w2.setBalance(new BigDecimal("50.00"));
        w2.setCurrencyType(CurrencyType.USD);
        walletRepository.save(w2);

        assertThrows(InsufficientFundsException.class, () -> {
            walletService.transferMoney(
                    w1.getId(),
                    w2.getId(),
                    new BigDecimal("100.00"),
                    "test-key-2"
            );
        });
    }
}