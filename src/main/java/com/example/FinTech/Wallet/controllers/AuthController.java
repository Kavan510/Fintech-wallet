package com.example.FinTech.Wallet.controllers;

import com.example.FinTech.Wallet.entity.User;
import com.example.FinTech.Wallet.repository.UserRepository;
import com.example.FinTech.Wallet.utils.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        // Logic to authenticate and return jwtUtil.generateToken(username)
        // ... (standard Spring Security authentication)
        return ResponseEntity.ok(Map.of("token", "YOUR_GENERATED_JWT_TOKEN"));
    }
}