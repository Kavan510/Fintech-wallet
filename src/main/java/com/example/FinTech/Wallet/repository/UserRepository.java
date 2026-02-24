package com.example.FinTech.Wallet.repository;

import com.example.FinTech.Wallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their unique username.
     * Essential for the Authentication process.
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a username is already taken during registration.
     */
    Boolean existsByUsername(String username);
}