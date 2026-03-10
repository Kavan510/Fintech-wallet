package com.example.FinTech.Wallet.service;


import com.example.FinTech.Wallet.entity.User;
import com.example.FinTech.Wallet.enums.Role;
import com.example.FinTech.Wallet.repository.UserRepository;
import com.example.FinTech.Wallet.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;


    public String register(User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
        }

        userRepository.save(user);

        return "User registered successfully";
    }

    public Map<String, String> login(String username, String password) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = JwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return Map.of("token", token);
    }


    public void logout(String token) {

        tokenBlacklistService.blacklistToken(token);
    }

}