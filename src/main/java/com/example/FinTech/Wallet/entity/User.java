package com.example.FinTech.Wallet.entity;

import com.example.FinTech.Wallet.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Id;


@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}