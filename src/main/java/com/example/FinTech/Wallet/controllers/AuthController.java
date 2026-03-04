package com.example.FinTech.Wallet.controllers;

import com.example.FinTech.Wallet.entity.User;
import com.example.FinTech.Wallet.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestParam String username,
            @RequestParam String password) {
        return ResponseEntity.ok(authService.login(username,password));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }


        String token = authHeader.substring(7);
        authService.logout(token);

        return ResponseEntity.ok("Logged out successfully");
    }
}